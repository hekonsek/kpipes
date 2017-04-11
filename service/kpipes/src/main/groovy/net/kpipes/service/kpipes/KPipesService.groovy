package net.kpipes.service.kpipes

import net.kpipes.core.adapter.Tenant
import net.kpipes.service.event.EventService
import net.kpipes.service.pipes.PipeService

import static net.kpipes.lib.commons.Mavens.artifactVersionFromDependenciesProperties

class KPipesService {

    private final EventService eventService

    private final PipeService pipeService

    KPipesService(EventService eventService, PipeService pipeService) {
        this.eventService = eventService
        this.pipeService = pipeService
    }

    String version() {
        artifactVersionFromDependenciesProperties('net.kpipes', 'kpipes-lib-commons').orElseThrow {
            new RuntimeException()
        }
    }

    Map<String, Object> dashboard(@Tenant String tenant) {
        def events = eventService.list(tenant)
        def pipes = pipeService.list(tenant)
        [events: events, pipes: pipes]
    }

}