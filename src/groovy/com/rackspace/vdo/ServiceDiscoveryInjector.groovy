package com.rackspace.vdo

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication

class ServiceDiscoveryInjector {
    /**
     * Singleton instance of the class.
     */
    private static ServiceDiscoveryInjector instance = null

    /**
     * Lock object for singleton instance access.
     */
    private static final Object instanceLock = new Object()

    /**
     * Return the instance of the injector.
     *
     * @return
     */
    static getInstance() {
        synchronized (instanceLock) {
            return instance
        }
    }

    /**
     * Set the instance of the injector.
     *
     * @param instance
     * @return
     */
    static setInstance(ServiceDiscoveryInjector instance) {
        synchronized (instanceLock) {
            this.instance = instance
        }
    }

    /**
     * Grails application bean.
     */
    GrailsApplication grailsApplication

    /**
     * Logger.
     */
    Logger log = Logger.getLogger(ServiceDiscoveryInjector)

    /**
     * List of the registered configuration sources.
     */
    List<ConfigSource> configSources = []

    /**
     * Thread responsible for running the update operation.
     */
    Thread updaterThread

    /**
     * The interval which the updater thread will use to update.
     *
     * A value <= 0 disables updating.
     */
    int updateInterval = 60000

    /**
     * The original application configuration.
     */
    ConfigObject originalConfiguration

    /**
     * Starts the configuration update operations.
     */
    void init() {
        originalConfiguration = cloneConfiguration(grailsApplication.config)

        updateConfiguration()

        if (updateInterval > 0) {
            updaterThread = Thread.startDaemon {
                while (true) {
                    try {
                        sleep updateInterval
                        updateConfiguration()
                    }
                    catch (Exception e) {
                        // TODO: do something with this.
                        log.error('TODO: bad things happened', e)
                    }
                }
            }
        }
    }

    /**
     * Stops the updater thread, if it exists.
     */
    void shutdown() {
        if (updaterThread) {
            updaterThread.stop()
        }
    }

    /**
     * Updates the application's configuration.
     */
    void updateConfiguration() {
        configSources*.update()

        grailsApplication.config.merge(mangleConfiguration(originalConfiguration))
    }

    /**
     * Updates the configuration and returns a new configuration root.
     *
     * @param parent
     * @return
     */
    ConfigObject mangleConfiguration(ConfigObject parent) {
        ConfigObject root = new ConfigObject()

        parent.each { key, value ->
            if (value instanceof Map && value.containsKey('configSource') && value.containsKey('path')) {
                String serviceId = value['configSource']

                ConfigSource configSource = configSources.find { it.getServiceId() == serviceId }
                if (!configSource) {
                    throw new Exception('unsupported config source type')
                }

                root.put(key, configSource.get(value['path']))
            }
            else if (value instanceof ConfigObject) {
                root.put(key, mangleConfiguration(value))
            }
            else {
                root.put(key, value)
            }
        }

        return root
    }

    /**
     * Add a new configuration source object.
     *
     * @param configSource
     */
    void addConfigSource(ConfigSource configSource) {
        if (!configSources.find { it.class == configSource.class }) {
            configSources << configSource
        }
    }

    /**
     * Performs a deep copy of configuration tree.
     *
     * @param object
     * @return
     */
    private ConfigObject cloneConfiguration(ConfigObject object) {
        ConfigObject copy = new ConfigObject()

        object.keySet().each { key ->
            def value = object.get(key)

            if (value instanceof ConfigObject) {
                value = cloneConfiguration(value)
            }

            copy.put(key, value)
        }
        return copy
    }

    void setUpdateInterval(def value) {
        if (value instanceof Integer) {
            this.updateInterval = value
        }
    }
}
