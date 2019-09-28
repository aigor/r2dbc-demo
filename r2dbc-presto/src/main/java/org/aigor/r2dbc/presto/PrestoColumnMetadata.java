package org.aigor.r2dbc.presto;

import io.r2dbc.spi.ColumnMetadata;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PrestoColumnMetadata implements ColumnMetadata {
    private final String name;

    @Override
    public String getName() {
        return name;
    }
}
