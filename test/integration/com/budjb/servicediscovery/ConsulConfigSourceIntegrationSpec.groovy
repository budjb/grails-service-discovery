package com.budjb.servicediscovery

import grails.test.spock.IntegrationSpec

class ConsulConfigSourceIntegrationSpec extends IntegrationSpec {
    ConsulConfigSource cs

    def setup() {
        cs = Spy(ConsulConfigSource)
        cs.url = 'http://23.253.48.67'
    }

    def 'Validate that a task is created and saved because a task is initialized'() {
        setup:
        cs.queryService() >> [['Key': 'web/key1', 'Value': 'dGVzdA==']]

        when:
        cs.update()

        then:
        cs.get('web/key1') == "test"
    }
}
