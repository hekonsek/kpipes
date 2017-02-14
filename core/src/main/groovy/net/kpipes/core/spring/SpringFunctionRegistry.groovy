package net.kpipes.core.spring

import net.kpipes.core.Function
import net.kpipes.core.FunctionRegistry
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationContext

class SpringFunctionRegistry implements FunctionRegistry {

    private ApplicationContext applicationContext

    SpringFunctionRegistry() {
        applicationContext = new SpringApplicationBuilder(SpringFunctionRegistryConfiguration).run()
    }

    @Override
    Function service(String id) {
        applicationContext.getBean(id, Function)
    }

    ApplicationContext getApplicationContext() {
        return applicationContext
    }

}