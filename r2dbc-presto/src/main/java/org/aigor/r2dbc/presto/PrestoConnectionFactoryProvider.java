package org.aigor.r2dbc.presto;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ConnectionFactoryProvider;

public class PrestoConnectionFactoryProvider implements ConnectionFactoryProvider {

    @Override
    public String getDriver() {
        return "Presto";
    }

    @Override
    public ConnectionFactory create(ConnectionFactoryOptions connectionFactoryOptions) {
        return new PrestoConnectionFactory(optionsToConfig(connectionFactoryOptions));
    }

    @Override
    public boolean supports(ConnectionFactoryOptions connectionFactoryOptions) {
        return true;
    }

    private PrestoConnectionConfiguration optionsToConfig(ConnectionFactoryOptions options) {
        // TODO: Map options to Presto configuration
        return null;
    }
}
