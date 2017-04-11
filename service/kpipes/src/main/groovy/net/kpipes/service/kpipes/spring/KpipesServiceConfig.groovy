package net.kpipes.service.kpipes.spring

import net.kpipes.service.event.EventService
import net.kpipes.service.kpipes.KPipesService
import net.kpipes.service.pipes.PipeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KpipesServiceConfig {

    @Bean(name = 'kpipes')
    KPipesService kpipesService(EventService eventService, PipeService pipeService) {
        new KPipesService(eventService, pipeService)
    }

}
