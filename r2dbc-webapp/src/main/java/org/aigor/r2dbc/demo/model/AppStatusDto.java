package org.aigor.r2dbc.demo.model;

import lombok.Value;

@Value
public class AppStatusDto {
    private final PoolStatusDto jdbc;
    private final PoolStatusDto r2dbc;
    private final int activeRequests;

    @Value
    public static class PoolStatusDto {
        private final int poolSize;
        private final int poolUsed;
        private final int poolQueueSize;
        private final int runningDbQueries;
    }
}
