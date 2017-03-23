package net.kpipes.core.spring

import net.kpipes.core.ServiceRegistry
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationContext

class SpringServiceRegistry implements ServiceRegistry {

    private ApplicationContext applicationContext

    SpringServiceRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
    }

    @Override
    Object service(String id) {
        applicationContext.getBean(id)
    }

    @Override
    <T> T service(Class<T> type) {
        applicationContext.getBean(type)
    }

    @Override
    <T> List<T> services(Class<T> type) {
        applicationContext.getBeansOfType(type).values().toList()
    }

}