package net.kpipes.core.starter

import groovy.transform.CompileStatic
import net.kpipes.core.starter.configuration.CommonsConfigurationResolver
import net.kpipes.core.starter.spi.ConfigurationResolver
import net.kpipes.core.starter.service.InMemoryServiceRegistry
import net.kpipes.core.starter.spi.Service
import net.kpipes.core.starter.spi.ServiceRegistry
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ConfigurationBuilder

import java.lang.reflect.Method

import static org.apache.commons.lang3.StringUtils.isNotBlank
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
        def startCallbacks = [:]
        reflections.getMethodsAnnotatedWith(Service).forEach { Method factoryMethod ->
            def serviceFactory = factoryMethod.declaringClass.newInstance()
            def service = factoryMethod.invoke(serviceFactory, this)
            serviceRegistry.registerService(service)

            def onStartCallback = factoryMethod.getAnnotation(Service).onStart()
            if(isNotBlank(onStartCallback)) {
                startCallbacks[service] = onStartCallback
            }
        }

        startCallbacks.each {
            def service = it.key
            def callbackName = it.value
            service.class.declaredMethods.find{ it.name == callbackName }.invoke(service)
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