package net.kpipes.core.starter

import groovy.transform.CompileStatic

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