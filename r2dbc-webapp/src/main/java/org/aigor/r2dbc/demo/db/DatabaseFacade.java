package org.aigor.r2dbc.demo.db;

import lombok.extern.slf4j.Slf4j;
import org.aigor.r2dbc.demo.db.jdbc.UsSalesJdbcRepository;
import org.aigor.r2dbc.demo.db.r2dbc.UsSalesR2dbcRepository;
import org.aigor.r2dbc.demo.model.StudyRequestDto;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.Duration.between;
import static java.time.Instant.now;

@Slf4j
public class DatabaseFacade {

    private final UsSalesJdbcRepository usSalesJdbcRepository;
    private final UsSalesR2dbcRepository usSalesR2dbcRepository;

    private final Scheduler jdbcScheduler;
    private final Scheduler r2dbcScheduler;

    public DatabaseFacade(
        UsSalesJdbcRepository usSalesJdbcRepository,
        UsSalesR2dbcRepository usSalesR2dbcRepository,
        @Qualifier("jdbcScheduler") Scheduler jdbcScheduler,
        @Qualifier("r2dbcScheduler") Scheduler r2dbcScheduler
    ) {
        this.usSalesJdbcRepository = usSalesJdbcRepository;
        this.usSalesR2dbcRepository = usSalesR2dbcRepository;
        this.jdbcScheduler = jdbcScheduler;
        this.r2dbcScheduler = r2dbcScheduler;
    }

    public Mono<UsSalesDataDto> resolvePersistedData(StudyRequestDto request, boolean reactive) {
        if (reactive) {
            return withTiming(usSalesR2Dbc(request.getRegion()), "R2DBC");
        } else {
            return withTiming(usSalesJdbc(request.getRegion()), "JDBC");
        }
    }

    private Mono<UsSalesDataDto> usSalesJdbc(String region) {
        return Mono.fromCallable(() ->
            usSalesJdbcRepository
                .findById(region)
                .orElse(null)
        ).subscribeOn(jdbcScheduler);
    }

    private Mono<UsSalesDataDto> usSalesR2Dbc(String region) {
        return usSalesR2dbcRepository
            .findById(region)
            .subscribeOn(r2dbcScheduler)
            .publishOn(r2dbcScheduler);
    }

    private static <T> Mono<T> withTiming(Mono<T> publisher, String repoType) {
        AtomicReference<Instant> startTime = new AtomicReference<>();
        return publisher
            .doOnSubscribe(__ -> startTime.set(now()))
            .doOnError(e -> log.warn("Error: ", e))
            .doFinally(__ ->
                log.debug("{} request finished, took: {}",
                    repoType, between(startTime.get(), now())));
    }
}
