package net.kpipes.core.starter

import groovy.transform.CompileStatic
import net.kpipes.core.starter.configuration.CommonsConfigurationResolver
import net.kpipes.core.starter.service.InMemoryServiceRegistry
import net.kpipes.core.starter.spi.ConfigurationResolver
import net.kpipes.core.starter.spi.KPipesOperations
import net.kpipes.core.starter.spi.ServiceRegistry

@CompileStatic
class KPipes implements KPipesOperations {

    private final ServiceRegistry serviceRegistry

    private final ConfigurationResolver configurationResolver

    KPipes(ServiceRegistry serviceRegistry, ConfigurationResolver configurationResolver) {
        this.serviceRegistry = serviceRegistry
        this.configurationResolver = configurationResolver
    }

    KPipes() {
        this(new InMemoryServiceRegistry(), new CommonsConfigurationResolver())
    }

    KPipes start() {
        serviceRegistry.start(this)
        this
    }

    ServiceRegistry serviceRegistry() {
        serviceRegistry
    }

    def <T> T service(Class<T> serviceType) {
        serviceRegistry.service(serviceType)
    }

    ConfigurationResolver configurationResolver() {
        configurationResolver
    }

}