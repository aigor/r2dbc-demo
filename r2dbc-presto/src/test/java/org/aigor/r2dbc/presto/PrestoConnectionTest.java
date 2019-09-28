package org.aigor.r2dbc.presto;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
class PrestoConnectionTest implements PrestoSampleQueries {

    @Test
    void simpleUsageExample() {
        var prestoConnectionFactory =
            new PrestoConnectionFactory(PrestoConnectionConfiguration.builder()
                .host("localhost")
                .port(8081)
                .user("root")
                .catalog("tpch")
                .schema("tiny")
                .build());

        Mono.from(prestoConnectionFactory.create())
            .doOnNext(conn ->
                log.info("Presto Version: {}", conn.getMetadata().getDatabaseVersion()))
            .flatMapMany(conn ->
                conn.createStatement(HELLO_QUERY).execute())
            .flatMap(result -> result.map(this::rowToString))
            .doOnNext(row -> log.info("[DATA ROW] {}", row))
            .then()
            .block();
    }

    @NotNull
    private String rowToString(Row row, RowMetadata rMeta) {
        var cMetas = rMeta.getColumnMetadatas();
        StringBuilder rowRep = new StringBuilder();
        for (var cMeta : cMetas) {
            if (!rowRep.toString().isBlank()) {
                rowRep.append(", ");
            }
            rowRep.append(cMeta.getName())
                .append(": ")
                .append(row.get(cMeta.getName()));
        }
        return rowRep.toString();
    }
}