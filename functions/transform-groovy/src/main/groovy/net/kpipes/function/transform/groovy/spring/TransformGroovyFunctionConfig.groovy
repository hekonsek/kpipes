package net.kpipes.function.transform.groovy.spring

import net.kpipes.function.transform.groovy.TransformGroovyFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TransformGroovyFunctionConfig {

    @Bean(name = 'transform.groovy')
    TransformGroovyFunction transformGroovyFunction() {
        new TransformGroovyFunction()
    }

}
