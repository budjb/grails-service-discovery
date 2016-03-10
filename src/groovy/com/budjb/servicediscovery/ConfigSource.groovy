package com.budjb.servicediscovery

interface ConfigSource {
    /**
     * Returns the value for the given key/path, if it exists.
     *
     * @param key The key or path of the configuration.
     * @return The value for the given key, or null.
     */
    String get(String key)

    /**
     * Returns all available key/value pairs availble for this configuration source.
     *
     * @return
     */
    Map<String, String> getAll()

    /**
     * Calls the external service containing service discovery information, and caches it locally.
     */
    void update()

    /**
     * The service ID of the config source, used by configurations to tell what service to use.
     *
     * @return
     */
    String getServiceId()
}
