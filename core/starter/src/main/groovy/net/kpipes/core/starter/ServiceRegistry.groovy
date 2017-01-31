package net.kpipes.core.starter

interface ServiceRegistry {

    def registerService(Object instance)

    def <T> T service(Class<T> serviceType)

}