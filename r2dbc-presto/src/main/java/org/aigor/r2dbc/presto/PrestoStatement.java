package org.aigor.r2dbc.presto;

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aigor.r2dbc.presto.model.JsonBodyHandler;
import org.aigor.r2dbc.presto.model.StatementResponse;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.json.bind.Jsonb;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.aigor.r2dbc.presto.PrestoUtils.uriForQueryPath;

@Slf4j
@RequiredArgsConstructor
public class PrestoStatement implements Statement {
    private static final String PRESTO_DOES_NOT_SUPPORT_BINDING = "Binding parameters is not supported";

    private final PrestoConnectionConfiguration conf;
    private final HttpClient client;
    private final Jsonb jsonb;
    private final String sql;

    @Override
    public Publisher<? extends Result> execute() {
        return Flux.defer(() -> {
            // Follow links (from JSON) applying timeouts
            // Parse metadata
            return sendSqlStatement(sql)
                .flatMap(statementResponse -> {
                    // Using atomic reference to track next location
                    String nextUri = statementResponse.getNextUri();
                    AtomicReference<String> nexUriRef = new AtomicReference<>(nextUri);

                    // Retry while statement response does not contain query results
                    return requestNextStatementStatus(nexUriRef)
                            .flatMap(this::responseDataOrRetrySignal)
                            .retryWhen(errStream -> errStream
                                .delayElements(Duration.ofMillis(250))
                                .filter(QueryQueuedException.class::isInstance)
                                .map(QueryQueuedException.class::cast)
                                .doOnNext(err -> nexUriRef.set(err.getNexUri()))
                            );
                })
                .doOnNext(response ->
                    log.debug("Response with data: \n  {}", response))
                .map(this::statementToPrestoResult);
        });
    }

    private PrestoResult statementToPrestoResult(StatementResponse statementResponse) {
        return new PrestoResult(statementResponse);
    }

    private Mono<StatementResponse> responseDataOrRetrySignal(StatementResponse response) {
        if (response.getStats().isQueued() || !response.hasData()) {
            return Mono.error(new QueryQueuedException(response.getNextUri()));
        } else {
            return Mono.just(response);
        }
    }

    private Mono<StatementResponse> sendSqlStatement(String sql) {
        log.debug("Submitting SQL statement: \n{}", sql);

        var statementRequest = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(sql))
            .uri(uriForQueryPath(conf, "statement"))
            .header("X-Presto-User", conf.getUser())
            .header("X-Presto-Catalog", conf.getCatalog())
            .header("X-Presto-Schema", conf.getSchema())
            .timeout(Duration.ofSeconds(30))
            .build();

        return Mono.fromCompletionStage(client.sendAsync(
                statementRequest,
                JsonBodyHandler.jsonBodyHandler(jsonb, StatementResponse.class)))
            .map(HttpResponse::body);
    }

    private Mono<StatementResponse> requestNextStatementStatus(AtomicReference<String> nextUri) {
        return Mono.defer(() -> {
            String actualNextUri = nextUri.get();
            return Mono.fromCompletionStage(
                client.sendAsync(
                    HttpRequest.newBuilder()
                        .uri(URI.create(actualNextUri))
                        .GET()
                        .build(),
                    JsonBodyHandler.jsonBodyHandler(jsonb, StatementResponse.class)))
                .map(HttpResponse::body)
                .doOnSubscribe(status ->
                    log.debug("Requesting data on URI: {}", actualNextUri));
        });
    }

    @Getter
    @RequiredArgsConstructor
    static class QueryQueuedException extends RuntimeException {
        private final String nexUri;
    }

    // --- Unsupported methods ---------------------------------

    @Override
    public Statement add() {
        return this;
    }

    @Override
    public Statement bind(int index, Object value) {
        throw new UnsupportedOperationException(PRESTO_DOES_NOT_SUPPORT_BINDING);
    }

    @Override
    public Statement bind(String name, Object value) {
        throw new UnsupportedOperationException(PRESTO_DOES_NOT_SUPPORT_BINDING);
    }

    @Override
    public Statement bindNull(int index, Class<?> type) {
        throw new UnsupportedOperationException(PRESTO_DOES_NOT_SUPPORT_BINDING);
    }

    @Override
    public Statement bindNull(String name, Class<?> type) {
        throw new UnsupportedOperationException(PRESTO_DOES_NOT_SUPPORT_BINDING);
    }
}
