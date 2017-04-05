package net.kpipes.core

import static java.util.Optional.empty
import static org.slf4j.LoggerFactory.getLogger

class PipeDefinitionEncoder {

    private static final LOG = getLogger(PipeDefinitionEncoder)

    static PipeDefinition decodePipe(String tenant, String pipeDefinitionText) {
        LOG.debug('Parsing pipe {} for tenant {}', pipeDefinitionText, tenant)
        List<String> definitionParts = pipeDefinitionText.split(/\|/).toList().collect{ it.trim() }
        String from = definitionParts[0]

        String[] functionParts = definitionParts[1].split(' ', 2)
        String functionAddress = functionParts[0]
        Map<String, Object> functionConfiguration = functionParts.length == 2 ? new GroovyShell().evaluate("L:${functionParts[1]}") as Map : [:]

        Optional<String> to = definitionParts.size() > 2 ? Optional.of(definitionParts[2]) : Optional.<String>empty()

        new PipeDefinition(tenant, empty(), from, functionAddress, functionConfiguration, to)
    }

    static String encodePipe(PipeDefinition definition) {
        String functionConfig = definition.functionConfiguration().isEmpty() ? '' : " ${definition.functionConfiguration().inspect()}"
        String encoded = "${definition.from()} | ${definition.functionAddress()}${functionConfig}"
        if(definition.to().present) {
            encoded = "${encoded} | ${definition.to().get()}"
        }
        encoded as String
    }

}