package org.aigor.r2dbc.demo.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aigor.r2dbc.demo.db.jdbc.UsSalesJdbcRepository;
import org.aigor.r2dbc.demo.db.r2dbc.UsSalesR2dbcRepository;
import org.aigor.r2dbc.demo.model.StudyRequestDto;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.Duration.between;
import static java.time.Instant.now;

@Slf4j
@RequiredArgsConstructor
public class DatabaseFacade {

    // Repositories for different DBs
    private final UsSalesJdbcRepository usSalesJdbcRepository;
    private final UsSalesR2dbcRepository usSalesR2dbcRepository;


    public Mono<UsSalesDataDto> resolvePersistedData(StudyRequestDto request) {
        switch (request.getStudy()) {
            case "usa-districts-jdbc":
                return withTiming(usSalesJdbc(request.getRegion()), "JDBC");
            case "usa-districts-r2dbc":
                return withTiming(usSalesR2Dbc(request.getRegion()), "R2DBC");
            default:
                return Mono.error(new RuntimeException("Unknown study: " + request.getStudy()));
        }
    }

    private Mono<UsSalesDataDto> usSalesJdbc(String region) {
        return Mono.fromCallable(() ->
            usSalesJdbcRepository
            .findById(region)
            .orElse(null)
        );
    }

    private Mono<UsSalesDataDto> usSalesR2Dbc(String region) {
        // Can not use findById as Binding parameters is not supported yet for Postgres R2DBC

        // return usSalesR2dbcRepository
        //   .findById(region)
        //   .map(UsSalesDataDto::getSales);

        // Used for batched & non-batched mode
        return usSalesR2dbcRepository
            .findAll()
            //.doOnNext(r -> log.debug(" [R2DBC -> App]: {}", r))
            .filter(data -> region.equals(data.getCode()))
            .next();
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
