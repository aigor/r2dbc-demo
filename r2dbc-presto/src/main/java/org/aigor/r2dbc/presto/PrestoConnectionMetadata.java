package org.aigor.r2dbc.presto;

import io.r2dbc.spi.ConnectionMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PrestoConnectionMetadata implements ConnectionMetadata {
    private final String databaseProductName;
    private final String databaseVersion;
}
