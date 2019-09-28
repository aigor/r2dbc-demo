package org.aigor.r2dbc.presto.model;

import lombok.Data;

import java.util.List;

@Data
public class StatementResponse {
    private String nextUri;
    private String partialCancelUri;

    private Stats stats;

    private List<ColumnMeta> columns;
    private List<List<Object>> data;

    public boolean hasData() {
        return data != null;
    }

    @Data
    public static class Stats {
        private String state;
        private boolean queued;
        private long elapsedTimeMillis;
    }

    @Data
    public static class ColumnMeta {
        private String name;
        private String type;
    }
}
