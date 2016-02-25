package com.rackspace.vdo

class ServiceDiscoveryValue {
    /**
     * Path or name of the value in the discovery service.
     */
    String path

    /**
     * ID of the service to use to gather the value.
     */
    String serviceId

    /**
     * Returns whether the service discovery value is complete.
     *
     * @return
     */
    boolean isValid() {
        return path && serviceId
    }
}
