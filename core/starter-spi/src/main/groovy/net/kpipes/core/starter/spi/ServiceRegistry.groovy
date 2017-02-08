package net.kpipes.core.starter.spi

interface ServiceRegistry {

    def start(KPipesOperations kpipes)

    def registerService(Object instance)

    def <T> T service(Class<T> serviceType)

}