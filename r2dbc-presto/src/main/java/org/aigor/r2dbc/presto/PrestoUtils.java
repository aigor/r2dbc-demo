package org.aigor.r2dbc.presto;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@UtilityClass
final class PrestoUtils {

    static URI uriForQueryPath(PrestoConnectionConfiguration config, String path) {
        URI uri = URI.create(String.format("http://%s:%d/v1/" + path, config.getHost(), config.getPort()));
        log.debug("Built URI: {}", uri.toString());
        return uri;
    }
}
