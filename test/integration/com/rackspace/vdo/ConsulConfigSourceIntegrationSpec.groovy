package com.rackspace.vdo.workflow

import com.rackspace.vdo.ConsulConfigSource
import grails.test.spock.IntegrationSpec

class ConsulConfigSourceIntegrationSpec extends IntegrationSpec {
    ConsulConfigSource cs

    def setup() {
        cs = new ConsulConfigSource()
        cs.baseUrl = "http://23.253.48.67/v1/kv"
    }

    def 'Validate that a task is created and saved because a task is initialized'() {
        setup:
        cs.update()

        when:
        String response = cs.get("web/key1")

        then:
        response == "test"
    }
}
