package org.aigor.r2dbc.demo;

import lombok.extern.slf4j.Slf4j;
import org.aigor.r2dbc.demo.db.DatabaseFacade;
import org.aigor.r2dbc.demo.model.AppStatusDto;
import org.aigor.r2dbc.demo.model.StudyRequestDto;
import org.aigor.r2dbc.demo.model.StudyResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.aigor.r2dbc.demo.utils.MonitoringUtils.toAppStatus;
import static org.aigor.r2dbc.demo.utils.SerializationUtils.parseRequest;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@EnableJdbcRepositories("org.aigor.r2dbc.demo.db.jdbc")
@EnableR2dbcRepositories
@Slf4j
@SpringBootApplication
public class ReactivePersistenceApplication {

	// Services
	private final ThreadPoolExecutor jdbcExecutor;
	private final ThreadPoolExecutor r2dbcExecutor;
	private final Scheduler jdbcScheduler;
	private final Scheduler r2dbcScheduler;

	private final DatabaseFacade dbFacade;

	@Autowired
	public ReactivePersistenceApplication(
		@Qualifier("jdbcWorker") ThreadPoolExecutor jdbcExecutor,
		@Qualifier("r2dbcWorker") ThreadPoolExecutor r2dbcExecutor,
		@Qualifier("jdbcScheduler") Scheduler jdbcScheduler,
		@Qualifier("r2dbcScheduler") Scheduler r2dbcScheduler,
		DatabaseFacade dbFacade
	) {
		this.jdbcExecutor = jdbcExecutor;
		this.r2dbcExecutor = r2dbcExecutor;
		this.jdbcScheduler = jdbcScheduler;
		this.r2dbcScheduler = r2dbcScheduler;
		this.dbFacade = dbFacade;
	}

	// Statistics
	private final AtomicInteger activeRequests = new AtomicInteger(0);

	public static void main(String[] args) {
		SpringApplication.run(ReactivePersistenceApplication.class, args);
	}

	@Bean
	public RouterFunction<?> routerFunction() {
		return RouterFunctions
			.route(
				GET("/"),
				request -> ok().render(
					"index",
					Rendering.view("index"))
			).andRoute(
				GET("/service/{study}/{region}"),
				request -> ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(
						withMetrics(processRequestBlocking(parseRequest(request))),
						StudyResultDto.class)
			).andRoute(
				GET("/nio/service/{study}/{region}"),
				request -> ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(
						withMetrics(processRequestReactive(parseRequest(request))),
						StudyResultDto.class)
            ).andRoute(
                GET("/status"),
                request -> ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(applicationStatus(), AppStatusDto.class)
			).andOther(
				resources("/**", new ClassPathResource("/static"))
			);
	}

	// --- Blocking handling ---------------------------------------------------

	private Mono<StudyResultDto> processRequestBlocking(StudyRequestDto studyRequest) {
		return Mono.fromCallable(() -> dbFacade
					.resolvePersistedData(studyRequest)
					.block())
			.map(persisted -> StudyResultDto.generic(persisted.getSales(), persisted.getSales())
			);
	}

	// ---- Async handling -----------------------------------------------------

	private Mono<StudyResultDto> processRequestReactive(StudyRequestDto studyRequest) {
			return dbFacade
				.resolvePersistedData(studyRequest)
				.map(persisted -> StudyResultDto.generic(persisted.getSales(), persisted.getSales()))
				.doOnError(e -> log.warn("Error:", e));
	}

	// --- App's metrics -------------------------------------------------------

	private Flux<AppStatusDto> applicationStatus() {
		return Flux.interval(Duration.ofMillis(250))
				.map(i -> toAppStatus(jdbcExecutor, r2dbcExecutor, activeRequests.get())
		);
	}

	private Mono<StudyResultDto> withMetrics(Mono<StudyResultDto> stream) {
		return stream
			.doOnSubscribe(s -> activeRequests.incrementAndGet())
			.doFinally(s -> activeRequests.decrementAndGet());
	}
}
