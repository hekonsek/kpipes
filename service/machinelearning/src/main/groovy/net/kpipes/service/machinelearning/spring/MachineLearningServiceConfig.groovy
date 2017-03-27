package net.kpipes.service.machinelearning.spring

import net.kpipes.core.KPipesConfig
import net.kpipes.lib.docker.Docker
import net.kpipes.service.machinelearning.MachineLearningService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MachineLearningServiceConfig {

    @Bean(name = 'machinelearning')
    MachineLearningService machineLearningService(KPipesConfig config, Docker docker) {
        new MachineLearningService(config, docker)
    }

}
