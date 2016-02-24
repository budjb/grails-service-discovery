package com.rackspace.vdo

import org.apache.log4j.Logger
import com.budjb.requestbuilder.RequestProperties


class ConsulConfigSource implements ConfigSource {
    String url
    String basePath
    Map <String, String> config

    Logger log = Logger.getLogger(ConsulConfigSource)


    /**
     * Returns the value for the given key/path, if it exists.
     *
     * @param key The key or path of the configuration.
     * @return The value for the given key, or null.
     */
    String get(String key) {
        return config[key]
    }


    /**
     * Returns all available key/value pairs available for this configuration source.
     *
     * @return
     */
    Map<String, String> getAll() {
        return config
    }

    /**
     * Calls the external service containing service discovery information, and caches it locally.
     */
    void update() {
        List<Map> response

        try {
            RequestProperties requestProperties = new RequestProperties()
            requestProperties.uri = "${url}/${basePath}/?recurse"
            requestProperties.accept = "application/json"
            requestProperties.ignoreInvalidSSL = true
            requestProperties.debug = true

            response = jerseyRequestBuilder.get(requestProperties)
        }
        catch (Exception e) {
            log.error("Failed to get consul config", e)
        }

        response.each{config[it.key] = it.value.bytes.decodeBase64().toString()}
    }
}