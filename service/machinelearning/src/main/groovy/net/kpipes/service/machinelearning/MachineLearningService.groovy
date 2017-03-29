package net.kpipes.service.machinelearning


import net.kpipes.core.adapter.Tenant
import net.kpipes.lib.commons.KPipesConfig
import net.kpipes.lib.docker.ContainerBuilder
import net.kpipes.lib.docker.Docker

import static net.kpipes.lib.commons.Mavens.kpipesVersion

class MachineLearningService {

    private final KPipesConfig kpipesConfig

    private final Docker docker

    MachineLearningService(KPipesConfig kpipesConfig, Docker docker) {
        this.kpipesConfig = kpipesConfig
        this.docker = docker
    }

    void train(@Tenant String tenant, String dataset, String model) {
        def volumes = [:]
        volumes[new File(kpipesConfig.applicationHome(), '/store/fileSystemKeyValue').absolutePath] = '/data'
        volumes[new File(kpipesConfig.applicationHome(), 'model').absolutePath] = '/model'
        def container = new ContainerBuilder("kpipes/machinelearning-train:${kpipesVersion()}").
                environment(DATA_ID: "${tenant}.${dataset}", MODEL_ID: "${tenant}_${model}").
                volumes(volumes).
                build()
        def results = docker.execute(container)
        if(results.last() != 'KPIPES:SUCCESS') {
            throw new IllegalStateException("Cannot execute training command: ${results}")
        }
    }

}
