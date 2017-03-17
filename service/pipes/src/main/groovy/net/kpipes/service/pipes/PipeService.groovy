package net.kpipes.service.pipes

import net.kpipes.adapter.websockets.Tenant
import net.kpipes.core.KPipesContext
import net.kpipes.core.repository.PipeDefinitionsRepository

import static net.kpipes.core.PipeDefinitionEncoder.decodePipe
import static net.kpipes.core.PipeDefinitionEncoder.encodePipe

class PipeService {

    private final KPipesContext kpipesContext

    private final PipeDefinitionsRepository pipeDefinitionsRepository

    PipeService(KPipesContext kpipesContext, PipeDefinitionsRepository pipeDefinitionsRepository) {
        this.kpipesContext = kpipesContext
        this.pipeDefinitionsRepository = pipeDefinitionsRepository
    }

    void create(@Tenant String tenant, String pipeDefinition) {
        kpipesContext.kpipes().addPipe(decodePipe(tenant, pipeDefinition))
    }

    List<String> list(@Tenant String tenant) {
        pipeDefinitionsRepository.list().findAll{ it.tenant() == tenant }.collect{ encodePipe(it) }
    }

}