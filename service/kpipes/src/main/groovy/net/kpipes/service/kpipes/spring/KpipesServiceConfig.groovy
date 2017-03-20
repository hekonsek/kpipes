package net.kpipes.service.kpipes.spring

import net.kpipes.service.kpipes.KPipesService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KpipesServiceConfig {

    @Bean(name = 'kpipes')
    KPipesService kpipesService() {
        new KPipesService()
    }

}
