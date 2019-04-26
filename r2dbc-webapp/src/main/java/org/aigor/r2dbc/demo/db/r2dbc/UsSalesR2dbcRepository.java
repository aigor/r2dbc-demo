package org.aigor.r2dbc.demo.db.r2dbc;


import org.aigor.r2dbc.demo.db.UsSalesDataDto;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsSalesR2dbcRepository extends R2dbcRepository<UsSalesDataDto, String> {

    @Override
    @Query("select * from us_sales_by_districts, pg_sleep(2)")
    Flux<UsSalesDataDto> findAll();

    // Binding parameters are not supported yet by r2dbc-postgres
    @Override
    @Query("select * from us_sales_by_districts, pg_sleep(2) where code=:code")
    Mono<UsSalesDataDto> findById(@Param("code") @NonNull String code);
}
