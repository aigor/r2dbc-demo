package org.aigor.r2dbc.presto;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ConnectionFactoryProvider;

public class PrestoConnectionFactoryProvider implements ConnectionFactoryProvider {

    @Override
    public String getDriver() {
        return "PrestoDB";
    }

    @Override
    public ConnectionFactory create(ConnectionFactoryOptions connectionFactoryOptions) {
        return new PrestoConnectionFactory();
    }

    @Override
    public boolean supports(ConnectionFactoryOptions connectionFactoryOptions) {
        return true;
    }
}
