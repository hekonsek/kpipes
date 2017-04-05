package net.kpipes.service.pipes

import net.kpipes.core.KPipes
import net.kpipes.core.adapter.Tenant
import net.kpipes.core.repository.PipeDefinitionsRepository

import static net.kpipes.core.PipeDefinitionEncoder.decodePipe
import static net.kpipes.core.PipeDefinitionEncoder.encodePipe

class PipeService {

    private final KPipes kPipes

    private final PipeDefinitionsRepository pipeDefinitionsRepository

    PipeService(KPipes kPipes, PipeDefinitionsRepository pipeDefinitionsRepository) {
        this.kPipes = kPipes
        this.pipeDefinitionsRepository = pipeDefinitionsRepository
    }

    void create(@Tenant String tenant, String pipeDefinition) {
        kPipes.addPipe(decodePipe(tenant, pipeDefinition))
    }

    void remove(String pipeId) {
        kPipes.removePipe(pipeId)
    }

    List<String> list(@Tenant String tenant) {
        pipeDefinitionsRepository.list().findAll{ it.tenant() == tenant }.collect{ "${it.id()}\t${encodePipe(it)}" as String }
    }

}