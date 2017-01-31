package net.kpipes.core.starter

import groovy.transform.CompileStatic
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ConfigurationBuilder

import java.lang.reflect.Method

import static org.reflections.util.ClasspathHelper.forPackage

@CompileStatic
class KPipes {

    private final ServiceRegistry serviceRegistry

    KPipes(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry
    }

    KPipes() {
        this(new InMemoryServiceRegistry())
    }

    KPipes start() {
        def reflections = new Reflections(new ConfigurationBuilder().
                setUrls(forPackage('.')).setScanners(new MethodAnnotationsScanner()))
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

}