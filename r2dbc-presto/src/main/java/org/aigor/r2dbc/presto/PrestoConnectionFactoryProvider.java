package org.aigor.r2dbc.presto;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ConnectionFactoryProvider;

public class PrestoConnectionFactoryProvider implements ConnectionFactoryProvider {

    @Override
    public ConnectionFactory create(ConnectionFactoryOptions connectionFactoryOptions) {
        return null;
    }

    @Override
    public boolean supports(ConnectionFactoryOptions connectionFactoryOptions) {
        return false;
    }
}
