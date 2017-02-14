package net.kpipes.core

class PipeDefinition {

    private final String from

    private final String functionAddress

    private final Map<String, Object> functionConfiguration

    private final String to

    PipeDefinition(String from, String functionAddress, Map<String, Object> functionConfiguration, String to) {
        this.from = from
        this.functionAddress = functionAddress
        this.functionConfiguration = functionConfiguration
        this.to = to
    }

    static PipeDefinition parsePipeDefinition(String pipeDefinitionText) {
        def definitionParts = pipeDefinitionText.split(/\|/).collect{ it.trim() }
        def from = definitionParts[0]

        def functionParts = definitionParts[1].split(' ', 2)
        def functionAddress = functionParts[0]
        def functionConfiguration = functionParts.size() == 2 ? new GroovyShell().evaluate("L:${functionParts[1]}") as Map : [:]

        def to = definitionParts[2]

        new PipeDefinition(from, functionAddress, functionConfiguration, to)
    }

    String from() {
        return from
    }

    String functionAddress() {
        return functionAddress
    }

    Map<String, Object> functionConfiguration() {
        return functionConfiguration
    }

    String to() {
        to
    }

}
