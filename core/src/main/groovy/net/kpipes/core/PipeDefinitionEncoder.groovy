package net.kpipes.core

class PipeDefinitionEncoder {

    static PipeDefinition decodePipe(String tenant, pipeDefinitionText) {
        def definitionParts = pipeDefinitionText.split(/\|/).collect{ it.trim() }
        String from = definitionParts[0]

        def functionParts = definitionParts[1].split(' ', 2)
        String functionAddress = functionParts[0]
        def functionConfiguration = functionParts.size() == 2 ? new GroovyShell().evaluate("L:${functionParts[1]}") as Map : [:]

        def to = definitionParts.size() > 2 ? Optional.of(definitionParts[2]) : Optional.empty()

        new PipeDefinition(tenant, from, functionAddress, functionConfiguration, to)
    }

    static String encodePipe(PipeDefinition definition) {
        def functionConfig = definition.functionConfiguration().isEmpty() ? '' : "${definition.functionConfiguration().toString()} "
        "${definition.from()} | ${definition.functionAddress()} ${functionConfig}| ${definition.to().get()}" as String
    }

}
