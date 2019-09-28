package org.aigor.r2dbc.presto;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aigor.r2dbc.presto.model.DbInfo;
import org.aigor.r2dbc.presto.model.JsonBodyHandler;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.aigor.r2dbc.presto.PrestoUtils.uriForQueryPath;

@Slf4j
@RequiredArgsConstructor
public class PrestoConnectionFactory implements ConnectionFactory {
    private final PrestoConnectionConfiguration config;

    @Override
    public ConnectionFactoryMetadata getMetadata() {
        return () -> "PrestoDB";
    }

    @Override
    public Publisher<PrestoConnection> create() {
        return Mono.defer(() -> {
            var client = buildHttpClient();
            var jsonb = JsonbBuilder.create();

            return retrieveDbServerInfo(client, jsonb)
                .map(s -> {
                    var prestoVersion = s.getNodeVersion().getVersion();
                    var dbMeta = new PrestoConnectionMetadata("PrestoDB", prestoVersion);
                    return new PrestoConnection(config, client, jsonb, dbMeta);
                });
        });
    }

    private HttpClient buildHttpClient() {
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NEVER)
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }

    private Mono<DbInfo> retrieveDbServerInfo(HttpClient client, Jsonb jsonb) {
        return Mono.fromCompletionStage(client.sendAsync(
                buildInfoRequest(),
                JsonBodyHandler.jsonBodyHandler(jsonb, DbInfo.class)))
            .map(HttpResponse::body);
    }

    private HttpRequest buildInfoRequest() {
        return HttpRequest.newBuilder()
            .GET()
            .uri(uriForQueryPath(config, "info"))
            .timeout(Duration.ofSeconds(3))
            .build();
    }
}
