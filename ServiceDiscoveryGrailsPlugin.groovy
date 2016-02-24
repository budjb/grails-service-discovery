import com.rackspace.vdo.ServiceDiscoveryInjector

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
     * Location of the plugin's issue tracker.
     */
    def issueManagement = [system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN"]

    /**
     * Online location of the plugin's browseable source code.
     */
    def scm = [url: "http://svn.codehaus.org/grails-plugins/"]

    /**
     * Web descriptor operations.
     */
    def doWithWebDescriptor = { xml ->
        // noop
    }

    /**
     * Spring initialization.
     */
    def doWithSpring = {
        ServiceDiscoveryInjector serviceDiscoveryInjector = new ServiceDiscoveryInjector()
        ServiceDiscoveryInjector.instance = serviceDiscoveryInjector

        serviceDiscoveryInjector.grailsApplication = application
        serviceDiscoveryInjector.addConfigSource(new Object()) /* TODO: the consul driver) */

        serviceDiscoveryInjector.init()
    }

    /**
     * Shutdown operations.
     */
    def onShutdown = { event ->
        application.mainContext.getBean('serviceDiscoveryInjector').shutdown()
    }
}
