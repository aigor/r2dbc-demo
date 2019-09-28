package org.aigor.r2dbc.presto.model;

import lombok.Data;

@Data
public class DbInfo {
    private NodeVersion nodeVersion;
    private String uptime;

    @Data
    public static class NodeVersion {
        private String version;
    }
}
