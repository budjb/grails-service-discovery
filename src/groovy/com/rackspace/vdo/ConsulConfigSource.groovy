package com.rackspace.vdo

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import groovyx.net.http.ResponseParseException
import org.apache.log4j.Logger

import java.text.ParseException

class ConsulConfigSource implements ConfigSource {
    /**
     * The URL to the Consul server.
     */
    String url

    /**
     * The base path of the configuration in Consul.
     */
    String basePath = ''

    /**
     * Configuration values provided by Consul.
     */
    Map<String, String> config = [:]

    /**
     * Logger.
     */
    Logger log = Logger.getLogger(ConsulConfigSource)

    /**
     * Sets the URL of the Consul server.
     *
     * @param url
     */
    void setUrl(def url) {
        if (url instanceof String) {
            this.url = url
        }
    }

    /**
     * Sets the base path of the configuration in Consul.
     *
     * @param path
     */
    void setBasePath(def path) {
        if (path instanceof String) {
            this.basePath = path
        }
    }

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
        if (!url) {
            throw new IllegalArgumentException('unable to poll Consul due to missing server URL')
        }

        HTTPBuilder httpBuilder = new HTTPBuilder(url)

        def response
        try {
            response = httpBuilder.get(path: buildConsulUri(), query: ['recurse': 1])

            if (!(response instanceof List)) {
                // TODO: better exception type?
                throw new Exception('response from Consul was not a list')
            }
        }
        catch (Exception e) {
            log.error("failed to get consul config", e)
            return
        }

        try {
            response.each { config[it['Key']] = new String(it['Value'].decodeBase64()) }
        }
        catch (Exception e) {
            log.error("unable to parse Consul response", e)
            return
        }
    }

    /**
     * Returns the service ID of the configuration source.
     *
     * @return
     */
    @Override
    String getServiceId() {
        return 'consul'
    }

    /**
     * Builds the URI to the Consul server.
     *
     * @return
     */
    String buildConsulUri() {
        return "/v1/kv/${basePath}".replaceAll(/\/\/+/, '/')
    }
}