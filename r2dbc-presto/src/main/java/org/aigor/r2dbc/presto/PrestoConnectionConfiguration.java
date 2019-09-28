package org.aigor.r2dbc.presto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PrestoConnectionConfiguration {
    private final String host;
    private final int port;
    private final String user;
    private final String catalog;
    private final String schema;
}
