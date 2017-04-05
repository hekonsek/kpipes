package net.kpipes.core.repository

import net.kpipes.core.PipeDefinition

interface PipeDefinitionsRepository {

    void add(PipeDefinition pipeDefinition)

    void remove(String id)

    List<PipeDefinition> list()

}
