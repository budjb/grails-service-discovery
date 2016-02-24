package com.rackspace.vdo

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication

class ServiceDiscoveryInjector {
    /**
     * Singleton instance of the class.
     *
     * TODO: make this thread safe
     */
    static ServiceDiscoveryInjector instance = null

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
     */
    int configurationUpdateInterval = 60000

    /**
     * The original application configuration.
     */
    ConfigObject originalConfiguration

    /**
     * Whether the application requests that the remote configuration sources update on an interval.
     */
    boolean updateRequested = false

    /**
     * Starts the configuration update operations.
     */
    void init() {
        originalConfiguration = deepcopy(grailsApplication.config)

        grailsApplication.config.merge(updateConfiguration(originalConfiguration))

        if (updateRequested) {
            updaterThread = Thread.startDaemon {
                while (true) {
                    try {
                        sleep configurationUpdateInterval

                        grailsApplication.config.merge(updateConfiguration(originalConfiguration))
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
     * Updates the configuration and returns a new configuration root.
     *
     * @param parent
     * @return
     */
    ConfigObject updateConfiguration(ConfigObject parent) {
        ConfigObject root = new ConfigObject()

        parent.each { key, value ->
            if (value instanceof List && value['configSource'] && value['path']) {
                String serviceId = value['configSource']

                ConfigSource configSource = configSources.find { it.getServiceId() == serviceId }
                if (!configSource) {
                    throw new Exception('unsupported config source type')
                }

                root.put(key, configSource.get(value['path']))
            }
            else if (value instanceof ConfigObject) {
                root.put(key, updateConfiguration(value))
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
    private ConfigObject deepcopy(ConfigObject object) {
        ConfigObject copy = new ConfigObject()
        object.keySet().each { key ->
            def value = object.get(key)
            if (value instanceof ConfigObject) {
                value = deepcopy(value)
            }
            copy.put(key, value)
        }
        return copy
    }
}
