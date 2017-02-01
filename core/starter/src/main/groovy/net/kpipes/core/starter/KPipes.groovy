package net.kpipes.core.starter

import groovy.transform.CompileStatic
import net.kpipes.core.starter.configuration.CommonsConfigurationResolver
import net.kpipes.core.starter.configuration.ConfigurationResolver
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ConfigurationBuilder

import java.lang.reflect.Method

import static org.reflections.util.ClasspathHelper.forJavaClassPath

@CompileStatic
class KPipes {

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
        def reflections = new Reflections(new ConfigurationBuilder().
                setUrls(forJavaClassPath()).setScanners(new MethodAnnotationsScanner()))
        reflections.getMethodsAnnotatedWith(Service).forEach { Method factoryMethod ->
            def serviceFactory = factoryMethod.declaringClass.newInstance()
            def service = factoryMethod.invoke(serviceFactory, this)
            serviceRegistry.registerService(service)
        }
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