package com.budjb.servicediscovery

class SystemConfigSource implements ConfigSource {
    /**
     * Returns the value for the given key/path, if it exists.
     *
     * @param key The key or path of the configuration.
     * @return The value for the given key, or null.
     */
    @Override
    String get(String key) {
        return System.getenv(key)
    }

    /**
     * Returns all available key/value pairs availble for this configuration source.
     *
     * @return
     */
    @Override
    Map<String, String> getAll() {
        return System.getenv()
    }

    /**
     * Calls the external service containing service discovery information, and caches it locally.
     */
    @Override
    void update() {
        // no need to do anything here
    }

    /**
     * The service ID of the config source, used by configurations to tell what service to use.
     *
     * @return
     */
    @Override
    String getServiceId() {
        return 'system'
    }
}
