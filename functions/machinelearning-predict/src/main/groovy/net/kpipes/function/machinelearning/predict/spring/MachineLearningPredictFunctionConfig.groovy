package net.kpipes.function.machinelearning.predict.spring

import net.kpipes.core.Json

import net.kpipes.function.machinelearning.predict.MachineLearningPredictFunction
import net.kpipes.lib.commons.KPipesConfig
import net.kpipes.lib.docker.Docker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MachineLearningPredictFunctionConfig {

    @Bean(name = 'machinelearning.predict')
    MachineLearningPredictFunction machineLearningPredictFunction(KPipesConfig config, Docker docker, Json json) {
        new MachineLearningPredictFunction(config, docker, json)
    }

}
