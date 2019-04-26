package org.aigor.r2dbc.demo;

import io.r2dbc.client.R2dbc;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class R2dbcSpiUsageTest {
    private static String INIT_DB =
        "create table if not exists trades (currency varchar(64), market varchar(64), price float)";

    private H2ConnectionFactory connectionFactory;

    private R2dbc r2dbcClient;

    @BeforeEach
    void setUp() {
        H2ConnectionConfiguration conf = H2ConnectionConfiguration.builder()
            .url("mem:db;DB_CLOSE_DELAY=-1;TRACE_LEVEL_FILE=4")
            .build();

        connectionFactory = new H2ConnectionFactory(conf);

        r2dbcClient = new R2dbc(connectionFactory);

        connectionFactory.create()
            .flatMapMany(conn ->
                conn.createStatement(INIT_DB)
                    .execute())
            .thenMany(
                connectionFactory.create()
                    .flatMapMany(conn ->
                        conn.createStatement("insert into trades (currency, market, price) VALUES (?, ?, ?)")
                            .bind(0, "USD").bind(1, "TX").bind(2, 2.0).add()
                            .bind(0, "UAH").bind(1, "TD").bind(2, 4.0).add()
                            .bind(0, "USD").bind(1, "TD").bind(2, 7.3)
                            .execute()
                    ))
            .then()
            .block();
    }

    @Test
    void usingR2dbcRawConnection() {
        connectionFactory
            .create()
            .flatMapMany(conn ->
                conn.createStatement("SELECT currency, price FROM trades")
                    .execute()
                    .flatMap(result -> result
                        .map((row, metadata) -> row.get("currency"))))
            .subscribe(row -> log.info(">>> {}", row));
    }

    @Test
    void usingR2dbcRawConnectionForInsertingData() {
        connectionFactory
            .create()
            .flatMapMany(conn ->
                conn.createStatement("insert into trades (currency, market, price) VALUES (?, ?, ?)")
                    .bind(0, "EUR").bind(1, "TD").bind(2, 7.0).add()
                    .bind(0, "UAH").bind(1, "TX").bind(2, 6.0).add()
                    .execute())
            .subscribe(
                _r -> {},
                _e -> {},
                () -> log.info("data inserted"));
    }

    @Test
    void usingR2dbcRawConnectionForTransaction() {
        connectionFactory
            .create()
            .flatMapMany(conn ->
                conn.beginTransaction()
                    .thenMany(conn.createStatement(
                            "INSERT INTO trades (currency, market, price) " +
                                "VALUES (?, ?, ?)")
                        .bind(0, "UAH").bind(1, "TX").bind(2, "B")
                        .execute())
                    .delayUntil(p -> conn.commitTransaction())
                    .onErrorResume(t -> conn
                        .rollbackTransaction()
                        .then(Mono.error(t))))
            .subscribe(
                _r -> {},
                e -> log.warn("error", e),
                () -> log.info("data inserted"));
    }

    @Test
    void usingR2dbcForUpdate() {
        r2dbcClient
            .withHandle(handle ->
                handle.createUpdate(
                       "INSERT INTO trades (currency, market, price) " +
                            "VALUES ($1, $2, $3)")
                        .bind("$1", "UAH").bind("$2", "TX").bind("$3", 3.4)
                        .execute())
            .subscribe(
                _r -> {},
                e -> log.warn("error", e),
                () -> log.info("data inserted"));
    }

    @Test
    void usingR2dbcForTransaction() {
        r2dbcClient
            .inTransaction(handle ->
                handle.createUpdate(
                        "INSERT INTO trades (currency, market, price) " +
                            "VALUES ($1, $2, $3)")
                    .bind("$1", "UAH").bind("$2", "TX").bind("$3", 3.4)
                    .execute())
            .subscribe(
                _r -> {},
                e -> log.warn("error", e),
                () -> log.info("data inserted"));
    }
}
