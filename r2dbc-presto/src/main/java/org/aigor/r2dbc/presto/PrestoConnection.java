package org.aigor.r2dbc.presto;

import io.r2dbc.spi.*;
import org.reactivestreams.Publisher;

public class PrestoConnection implements Connection {

    @Override
    public ConnectionMetadata getMetadata() {
        // Cache PrestoDB version
        return null;
    }

    @Override
    public Publisher<Boolean> validate(ValidationDepth depth) {
        // Validate connection
        return null;
    }

    @Override
    public Statement createStatement(String sql) {
        return null;
    }

    @Override
    public Publisher<Void> close() {
        return null;
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
