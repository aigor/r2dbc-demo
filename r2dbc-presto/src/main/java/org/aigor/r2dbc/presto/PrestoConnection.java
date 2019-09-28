package org.aigor.r2dbc.presto;

import io.r2dbc.spi.*;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.json.bind.Jsonb;
import java.net.http.HttpClient;

@RequiredArgsConstructor
public class PrestoConnection implements Connection {

    private final PrestoConnectionConfiguration conf;
    private final HttpClient httpClient;
    private final Jsonb jsonb;
    private final PrestoConnectionMetadata connectionMetadata;

    @Override
    public ConnectionMetadata getMetadata() {
        return connectionMetadata;
    }

    @Override
    public Statement createStatement(String sql) {
        return new PrestoStatement(conf, httpClient, jsonb, sql);
    }

    @Override
    public Publisher<Boolean> validate(ValidationDepth depth) {
        // TODO: Validate connection by making version call
        return Mono.just(true);
    }

    @Override
    public Publisher<Void> close() {
        // TODO: Clean-up resources, if any
        return Mono.empty();
    }

    // --- Not implemented

    @Override
    public Batch createBatch() {
        return null;
    }

    // --- Transaction management (not-relevant)

    @Override
    public Publisher<Void> beginTransaction() {
        return null;
    }

    @Override
    public Publisher<Void> commitTransaction() {
        return null;
    }

    @Override
    public Publisher<Void> createSavepoint(String name) {
        return null;
    }

    @Override
    public boolean isAutoCommit() {
        return false;
    }

    @Override
    public Publisher<Void> setAutoCommit(boolean autoCommit) {
        return null;
    }

    @Override
    public IsolationLevel getTransactionIsolationLevel() {
        return null;
    }

    @Override
    public Publisher<Void> releaseSavepoint(String name) {
        return null;
    }

    @Override
    public Publisher<Void> rollbackTransaction() {
        return null;
    }

    @Override
    public Publisher<Void> rollbackTransactionToSavepoint(String name) {
        return null;
    }

    @Override
    public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
        return null;
    }
}
