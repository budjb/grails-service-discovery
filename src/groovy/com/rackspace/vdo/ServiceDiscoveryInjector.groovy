package com.rackspace.vdo

interface ServiceDiscoveryInjector {
    /**
     * Called to initialize the injection system.
     *
     * This method will start an updater thread if configured to do so, which will
     * update the application's configuration against external services at a configured
     * interval time.
     *
     * @param configuration
     */
    void init(ConfigObject configuration)

    /**
     * Called when the application is shutting down. This method cleanly stops the updater
     * thread, if it is active.
     */
    void shutdown()

    /**
     * When called, update the original Grails configuration with new values from
     * external service discovery services.
     */
    void updateConfiguration()

    /**
     * Add a configuration source object to the list of supported service discovery services.
     *
     * @param configSource
     */
    void addConfigSource(ConfigSource configSource)

    /**
     * Return the list of all registered configuration sources.
     *
     * @return
     */
    List<ConfigSource> getConfigSources()

    /**
     * Return the instance of a configuration source for the given class type.
     *
     * @param type
     * @return
     */
    ConfigSource getConfigSource(Class<ConfigSource> type)
}