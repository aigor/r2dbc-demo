package org.aigor.r2dbc.presto;

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import org.aigor.r2dbc.presto.model.StatementResponse;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PrestoResult implements Result {
    private final List<List<Object>> data;
    private final PrestoRowMetadata rowMetadata;

    public PrestoResult(StatementResponse statementResponse) {
        this.data = statementResponse.getData();
        this.rowMetadata = new PrestoRowMetadata(
            statementResponse.getColumns().stream()
            .map(cm -> new PrestoColumnMetadata(cm.getName()))
            .collect(Collectors.toList())
        );
    }

    @Override
    public Publisher<Integer> getRowsUpdated() {
        return Mono.just(0);
    }

    @Override
    public <T> Publisher<T> map(BiFunction<Row, RowMetadata, ? extends T> mappingFunction) {
        return Flux.fromIterable(data)
            .map(rowData -> mappingFunction.apply(new PrestoRow(rowMetadata, rowData), rowMetadata));
    }
}
