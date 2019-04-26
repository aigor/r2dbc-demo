package org.aigor.r2dbc.demo.trade;

import io.r2dbc.client.R2dbc;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
public class H2TradeRepository implements TradeRepository {

	private static String INIT_DB =
		"create table trades (currency varchar(64), market varchar(64), price float)";

	private final R2dbc h2Client;

	public H2TradeRepository() {
		H2ConnectionConfiguration conf = H2ConnectionConfiguration.builder()
			.url("mem:db;DB_CLOSE_DELAY=-1;TRACE_LEVEL_FILE=4")
			.build();

		H2ConnectionFactory h2ConnectionFactory = new H2ConnectionFactory(conf);

		h2Client = new R2dbc(h2ConnectionFactory);

		initDB();
		pingDB();
	}

	private void initDB() {
		h2Client.inTransaction(session -> session
			.execute(INIT_DB)
			.doOnNext(i -> log.info("DB SCHEMA INITIALIZED"))
		).blockLast();
	}

	private void pingDB() {
		h2Client.withHandle(t -> t
			.createQuery("SELECT 6")
			.mapResult(result -> result.map((row, metadata) -> row.get(0))))
			.doOnNext(e -> log.info("RESULT FOR SELECT 6 QUERY: " + e))
			.subscribe();
	}

	public Mono<Void> saveAll(Flux<Trade> trades) {
		return trades
			.doOnNext(t -> log.info("[SQL] Requested to save trade: " + t))
			.flatMap(this::save)
			.then();
	}

	public Mono<Void> save(Trade trade) {
		return h2Client.inTransaction(session ->
			session
				.execute(
					"INSERT INTO trades (currency, market, price) VALUES (?, ?, ?)",
					trade.getCurrency(),
					trade.getMarket(),
					trade.getPrice())
				.doOnError(Throwable::printStackTrace)
				.doOnNext(i -> log.info("[SQL] Inserted rows: " + i))
		).then();
	}

	public Mono<Void> showRepoAnalytics() {
		return h2Client.withHandle(handle -> handle
				.createQuery("SELECT market, COUNT(*) FROM trades GROUP BY market")
				.mapResult(res -> res
					.map(this::mapMarketStats)))
				.reduce((s1, s2) -> s1 + ", " + s2)
				.doOnNext(e -> log.info("[SQL] [*] Stored trades: " + e)
		).then();
	}

	private String mapMarketStats(Row row, RowMetadata meta) {
		return "" + row.get(0) + ": " + row.get(1) +
			" (" + meta.getColumnMetadata(0).getName() + ")";
	}
}
