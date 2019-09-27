package org.aigor.r2dbc.presto;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class PrestoConnectionFactory implements ConnectionFactory {

    @Override
    public ConnectionFactoryMetadata getMetadata() {
        return () -> "PrestoDB";
    }

    @Override
    public Publisher<PrestoConnection> create() {
        return Mono.defer(() -> Mono.just(new PrestoConnection()));
    }
}
