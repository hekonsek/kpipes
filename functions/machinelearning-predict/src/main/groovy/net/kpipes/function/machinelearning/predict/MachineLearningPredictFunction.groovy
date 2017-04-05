package net.kpipes.function.machinelearning.predict

import com.google.common.io.Files
import net.kpipes.core.Json

import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import net.kpipes.lib.commons.KPipesConfig
import net.kpipes.lib.docker.ContainerBuilder
import net.kpipes.lib.docker.Docker

import static com.google.common.io.Files.toByteArray
import static net.kpipes.lib.commons.Mavens.kpipesVersion

class MachineLearningPredictFunction implements EventMappingFunction {

    private final KPipesConfig kpipesConfig

    private final Docker docker

    private final Json json

    MachineLearningPredictFunction(KPipesConfig kpipesConfig, Docker docker, Json json) {
        this.kpipesConfig = kpipesConfig
        this.docker = docker
        this.json = json
    }

    @Override
    Map<String, Object> onEvent(Event event) {
        def tenant = event.tenant()
        def model = event.config().model as String
        def volumes = [:]
        def eventDirectory = Files.createTempDir()
        Files.write(json.asBytesArray(event.body().featureVector), new File(eventDirectory, 'request.json'))
        volumes[eventDirectory.absolutePath] = '/event'
        volumes[new File(kpipesConfig.applicationHome(), 'model').absolutePath] = '/model'
        def container = new ContainerBuilder("kpipes/machinelearning-predict:${kpipesVersion()}").
                environment(MODEL_ID: "${tenant}_${model}").
                volumes(volumes).
                build()
        def results = docker.execute(container)
        if(results.last() != 'KPIPES:SUCCESS') {
            throw new IllegalStateException("Cannot execute predict command: ${results}")
        }
        event.body()['response.machinelearning.predict'] = json.read(toByteArray(new File(eventDirectory, 'response.json'))).response
        event.body()
    }

}
