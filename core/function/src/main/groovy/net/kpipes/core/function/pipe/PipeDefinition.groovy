package net.kpipes.core.function.pipe

class PipeDefinition {

    private final String id

    private final String from

    private final String functionAddress

    private final Map<String, Object> functionConfiguration

    private final Optional<String> to

    PipeDefinition(String id, String from, String functionAddress, Map<String, Object> functionConfiguration, Optional<String> to) {
        this.id = id
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

        def to = definitionParts.size() == 3 ? Optional.of(definitionParts[2]) : Optional.empty()

        new PipeDefinition(pipeDefinitionText, from, functionAddress, functionConfiguration, to)
    }

    String id() {
        return id
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

    Optional<String> to() {
        return to
    }

}
