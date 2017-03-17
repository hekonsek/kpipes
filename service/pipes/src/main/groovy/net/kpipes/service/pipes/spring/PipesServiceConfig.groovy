package net.kpipes.service.pipes.spring

import net.kpipes.core.KPipesContext
import net.kpipes.core.repository.PipeDefinitionsRepository
import net.kpipes.service.pipes.PipeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PipesServiceConfig {

    @Bean(name = 'pipe')
    PipeService pipesService(KPipesContext kPipesContext, PipeDefinitionsRepository pipeDefinitionsRepository) {
        new PipeService(kPipesContext, pipeDefinitionsRepository)
    }

}
