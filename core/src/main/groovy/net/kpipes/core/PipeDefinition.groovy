package net.kpipes.core

class PipeDefinition {

    private final String tenant

    private final String from

    private final String functionAddress

    private final Map<String, Object> functionConfiguration

    private final Optional<String> to

    PipeDefinition(String tenant, String from, String functionAddress, Map<String, Object> functionConfiguration, Optional<String> to) {
        this.tenant = tenant
        this.from = from
        this.functionAddress = functionAddress
        this.functionConfiguration = functionConfiguration
        this.to = to
    }

    String id() {
        (from + functionAddress + functionConfiguration + to.orElse("")).replaceAll(":", "_")
    }

    String tenant() {
        tenant
    }

    String effectiveFrom() {
        "${tenant}.${from}" as String
    }

    String from() {
        from
    }

    String functionAddress() {
        return functionAddress
    }

    Map<String, Object> functionConfiguration() {
        return functionConfiguration
    }

    Optional<String> effectiveTo() {
        if(to.present)
            Optional.of("${tenant}.${to.get()}" as String)
        else
            Optional.empty()
    }

    Optional<String> to() {
        to
    }

}
