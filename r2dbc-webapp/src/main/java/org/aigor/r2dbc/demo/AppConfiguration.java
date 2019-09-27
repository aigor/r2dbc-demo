package org.aigor.r2dbc.demo;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.aigor.r2dbc.demo.db.DatabaseFacade;
import org.aigor.r2dbc.demo.db.jdbc.UsSalesJdbcRepository;
import org.aigor.r2dbc.demo.db.r2dbc.UsSalesR2dbcRepository;
import org.aigor.r2dbc.demo.utils.AppSchedulers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class AppConfiguration {
    static final String JDBC_WORKER_NAME = "jdbc";
    static final String R2DBC_WORKER_NAME = "r2dbc";

    // --- Services ------------------------------------------------------------

    @Bean
    public DatabaseFacade databaseFacade(
        UsSalesJdbcRepository usSalesJdbcRepository,
        UsSalesR2dbcRepository usSalesR2dbcRepository,
        @Qualifier("jdbcScheduler") Scheduler jdbcScheduler,
        @Qualifier("r2dbcScheduler") Scheduler r2dbcScheduler
    ) {
        return new DatabaseFacade(
            usSalesJdbcRepository,
            usSalesR2dbcRepository,
            jdbcScheduler,
            r2dbcScheduler
        );
    }

    // --- R2DBC configuration -------------------------------------------------
    @Bean
    public DatabaseClient databaseClient(
        @Value("${spring.datasource.url}") String url,
        @Value("${spring.datasource.username}") String user,
        @Value("${spring.datasource.password}") String password
    ) {
        // Parse database connection params into R2DBC friendly format
        var host = url.substring(url.indexOf("//") + 2, url.lastIndexOf(":"));
        var port = Integer.parseInt(url.substring(url.lastIndexOf(":") + 1, url.lastIndexOf("/")));
        var database = url.substring(url.lastIndexOf("/") + 1);

        return databaseClient(host, port, database, user, password);
    }

    private DatabaseClient databaseClient(
        String host,
        int port,
        String database,
        String user,
        String password
    ) {
        log.info("Reactive Postgres config. Host: '{}', DB: '{}', user: '{}'", host, database, user);

        var connectionFactory =
            new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .username(user)
                .password(password).build());

        return DatabaseClient.create(connectionFactory);
    }

    @Bean
    public ReactiveDataAccessStrategy reactiveDataAccessStrategy() {
        return new DefaultReactiveDataAccessStrategy(PostgresDialect.INSTANCE);
    }

    @Bean
    public R2dbcRepositoryFactory factory(
        DatabaseClient client,
        ReactiveDataAccessStrategy reactiveDataAccessStrategy
    ) {
        return new R2dbcRepositoryFactory(client, reactiveDataAccessStrategy);
    }

    @Bean
    public UsSalesR2dbcRepository repository(R2dbcRepositoryFactory factory) {
        return factory.getRepository(UsSalesR2dbcRepository.class);
    }

    // --- Workers, http clients -----------------------------------------------

    @Bean("jdbcWorker")
    public ThreadPoolExecutor jdbcExecutor(@Value("${worker.jdbc.size}") int workerSize){
        return AppSchedulers.newExecutor(JDBC_WORKER_NAME, workerSize);
    }

    @Bean("jdbcScheduler")
    public Scheduler jdbcScheduler(@Qualifier("jdbcWorker") ThreadPoolExecutor executor) {
        return Schedulers.fromExecutor(executor);
    }

    @Bean("r2dbcWorker")
    public ThreadPoolExecutor r2dbcExecutor(@Value("${worker.r2dbc.size}") int workerSize){
        return AppSchedulers.newExecutor(R2DBC_WORKER_NAME, workerSize);
    }

    @Bean("r2dbcScheduler")
    public Scheduler r2dbcScheduler(@Qualifier("r2dbcWorker") ThreadPoolExecutor executor) {
        return Schedulers.fromExecutor(executor);
    }

}
