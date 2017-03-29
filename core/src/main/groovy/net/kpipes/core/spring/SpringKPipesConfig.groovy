package net.kpipes.core.spring

import net.kpipes.core.EventEncoder
import net.kpipes.core.Json
import net.kpipes.core.JsonEventEncoder
import net.kpipes.core.KPipes

import net.kpipes.core.PipeBuilder
import net.kpipes.core.ServiceRegistry
import net.kpipes.lib.commons.KPipesConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean

@SpringBootApplication(scanBasePackages = 'net.kpipes')
class SpringKPipesConfig {

    @Bean
    KPipes kpipesContext(ServiceRegistry serviceRegistry, PipeBuilder pipeBuilder) {
        new KPipes(serviceRegistry, pipeBuilder)
    }

    @Bean
    ServiceRegistry serviceRegistry(ApplicationContext applicationContext) {
        new SpringServiceRegistry(applicationContext)
    }

    @Bean
    PipeBuilder pipeBuilder(KPipesConfig config, ServiceRegistry serviceRegistry) {
        new PipeBuilder(config, serviceRegistry)
    }

    @Bean
    Json json() {
        new Json()
    }

    @ConditionalOnMissingBean
    @Bean
    EventEncoder eventEncoder() {
        new JsonEventEncoder()
    }

}
