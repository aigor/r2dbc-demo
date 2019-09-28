package org.aigor.r2dbc.presto;

import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PrestoRowMetadata implements RowMetadata {
    private final List<ColumnMetadata> columnMetadata;

    public int getColumnIndex(String name) {
        for (int i = 0; i < columnMetadata.size(); i++) {
             if (name.equals(columnMetadata.get(i).getName())) {
                 return i;
             }
        }
        throw new RuntimeException("Can not find column " + name);
    }

    @Override
    public ColumnMetadata getColumnMetadata(int index) {
        return columnMetadata.get(index);
    }

    @Override
    public ColumnMetadata getColumnMetadata(String name) {
        return columnMetadata.stream()
            .filter(column -> name.equals(column.getName()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Can not find column " + name));
    }

    @Override
    public Iterable<? extends ColumnMetadata> getColumnMetadatas() {
        return Collections.unmodifiableList(columnMetadata);
    }

    @Override
    public Collection<String> getColumnNames() {
        return columnMetadata.stream()
            .map(ColumnMetadata::getName)
            .collect(Collectors.toList());
    }
}
