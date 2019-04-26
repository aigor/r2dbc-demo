package org.aigor.r2dbc.demo.utils;

import org.aigor.r2dbc.demo.model.AppStatusDto;

import java.util.concurrent.ThreadPoolExecutor;

public final class MonitoringUtils {
    private MonitoringUtils() { }

    public static AppStatusDto toAppStatus(
        ThreadPoolExecutor jdbcExecutor,
        ThreadPoolExecutor r2dbcExecutor,
        int activeRequests
    ) {
        return new AppStatusDto(
            new AppStatusDto.PoolStatusDto(
                jdbcExecutor.getMaximumPoolSize(),
                jdbcExecutor.getActiveCount(),
                jdbcExecutor.getQueue().size()),

            new AppStatusDto.PoolStatusDto(
                r2dbcExecutor.getMaximumPoolSize(),
                r2dbcExecutor.getActiveCount(),
                r2dbcExecutor.getQueue().size()),

            activeRequests
        );
    }
}
