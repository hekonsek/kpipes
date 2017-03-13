package net.kpipes.core

class PipeDefinition {

    private final String from

    private final String functionAddress

    private final Map<String, Object> functionConfiguration

    private final Optional<String> to

    PipeDefinition(String from, String functionAddress, Map<String, Object> functionConfiguration, Optional<String> to) {
        this.from = from
        this.functionAddress = functionAddress
        this.functionConfiguration = functionConfiguration
        this.to = to
    }

    static PipeDefinition parsePipeDefinition(String tenant, pipeDefinitionText) {
        def definitionParts = pipeDefinitionText.split(/\|/).collect{ it.trim() }
        String from = "${tenant}.${definitionParts[0]}" as String

        def functionParts = definitionParts[1].split(' ', 2)
        String functionAddress = functionParts[0]
        def functionConfiguration = functionParts.size() == 2 ? new GroovyShell().evaluate("L:${functionParts[1]}") as Map : [:]

        def to = definitionParts.size() > 2 ? Optional.of("${tenant}.${definitionParts[2]}" as String) : Optional.empty()

        new PipeDefinition(from, functionAddress, functionConfiguration, to)
    }

    String id() {
        (from + functionAddress + functionConfiguration + to.orElse("")).replaceAll(":", "_")
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
        to
    }

}
