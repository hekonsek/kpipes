package net.kpipes.core.starter.service

import groovy.transform.CompileStatic
import net.kpipes.core.starter.spi.ServiceRegistry

@CompileStatic
class InMemoryServiceRegistry implements ServiceRegistry {

    private final Map<String, Object> services = [:]

    def registerService(Object instance) {
        services[instance.class.name] = instance
    }

    def <T> T service(Class<T> serviceType) {
        (T) services.values().find{ serviceType.isAssignableFrom(it.class) }
    }

}