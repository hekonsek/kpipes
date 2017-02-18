package net.kpipes.core.spring

import net.kpipes.core.FunctionRegistry
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationContext

class SpringFunctionRegistry implements FunctionRegistry {

    private ApplicationContext applicationContext

    SpringFunctionRegistry() {
        applicationContext = new SpringApplicationBuilder(SpringFunctionRegistryConfiguration).run()
    }

    @Override
    Object service(String id) {
        applicationContext.getBean(id)
    }

    @Override
    <T> T service(Class<T> type) {
        applicationContext.getBean(type)
    }

    ApplicationContext getApplicationContext() {
        return applicationContext
    }

}