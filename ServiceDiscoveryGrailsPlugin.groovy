import com.rackspace.vdo.ConsulConfigSource
import com.rackspace.vdo.ServiceDiscoveryInjector
import org.codehaus.groovy.grails.commons.GrailsApplication

class ServiceDiscoveryGrailsPlugin {
    /**
     * Plugin version.
     */
    def version = "0.1.0"

    /**
     * Grails version requirement.
     */
    def grailsVersion = "2.4 > *"

    /**
     * Excluded files.
     */
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    /**
     * Plugin title.
     */
    def title = "Service Discovery Plugin"

    /**
     * Author name.
     */
    def author = "Bud Byrd"

    /**
     * Author email address.
     */
    def authorEmail = "bud.byrd@gmail.com"

    /**
     * Plugin description.
     */
    def description = 'A plugin that allows service discovery injection into Grails configuration at runtime.'

    /**
     * Plugin documentation URL.
     */
    def documentation = "http://grails.org/plugin/service-discovery"

    /**
     * Plugin license.
     */
    def license = "APACHE"

    /**
     * Additional developers.
     */
    def developers = [
        [name: "Cameron Lopez", email: "cameronjlopez@gmail.com"],
        [name: "Ian Logan", email: "xeregin@gmail.com"],
        [name: "Michael Xin", email: "jqxin2006@gmail.com"]
    ]

    /**
     * Plugin load order
     */
    def loadBefore = ['logging']

    /**
     * Plugin load order
     */
    def loadAfter = ['core']

    /**
     * Location of the plugin's issue tracker.
     */
    def issueManagement = [system: "github", url: "https://github.com/budjb/grails-service-discovery/issues"]

    /**
     * Online location of the plugin's browseable source code.
     */
    def scm = [url: "https://github.com/budjb/grails-service-discovery"]

    /**
     * Web descriptor operations.
     */
    def doWithWebDescriptor = { xml ->
        installServiceDiscovery(application, false)
    }

    /**
     * Spring initialization.
     */
    def doWithSpring = {
        installServiceDiscovery(application, true)
    }

    /**
     * Shutdown operations.
     */
    def onShutdown = { event ->
        event.ctx.getBean('serviceDiscoveryInjector').shutdown()
    }

    /**
     * Creates, configures, and updates application configuration with service discovery.
     *
     * @param application
     * @param startUpdating
     * @return
     */
    def installServiceDiscovery(GrailsApplication application, boolean startUpdating) {
        def enabled = application.config.serviceDiscovery.enabled
        if (enabled instanceof Boolean && !enabled) {
            return
        }

        ConsulConfigSource consulConfigSource = new ConsulConfigSource()
        consulConfigSource.url = application.config.serviceDiscovery.consul.url
        consulConfigSource.basePath = application.config.serviceDiscovery.consul.basePath

        ServiceDiscoveryInjector serviceDiscoveryInjector = new ServiceDiscoveryInjector()
        serviceDiscoveryInjector.grailsApplication = application
        serviceDiscoveryInjector.updateInterval = startUpdating ? application.config.serviceDiscovery.updateInterval : 0
        serviceDiscoveryInjector.addConfigSource(consulConfigSource)

        ServiceDiscoveryInjector.setInstance(serviceDiscoveryInjector)
        serviceDiscoveryInjector.init()
    }
}
