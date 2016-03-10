package com.budjb.servicediscovery

import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Created by budbyrd on 2/24/16.
 */
class IndexController {
    GrailsApplication grailsApplication

    def index() {
        render grailsApplication.config.vdo.rules
    }
}
