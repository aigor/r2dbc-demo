package org.aigor.r2dbc.presto;

import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PrestoRow implements Row {
    private final PrestoRowMetadata prestoRowMetadata;
    private final List<Object> data;

    @Override
    public <T> T get(int index, Class<T> type) {
        return type.cast(data.get(index));
    }

    @Override
    public <T> T get(String name, Class<T> type) {
        int columnIndex = prestoRowMetadata.getColumnIndex(name);
        return get(columnIndex, type);
    }
}
