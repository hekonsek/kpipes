package net.kpipes.core

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex

class PipeDefinition {

    private final String tenant

    private final Optional<String> id

    private final String from

    private final String functionAddress

    private final Map<String, Object> functionConfiguration

    private final Optional<String> to

    PipeDefinition(String tenant, Optional<String> id, String from, String functionAddress, Map<String, Object> functionConfiguration, Optional<String> to) {
        this.tenant = tenant
        this.id = id
        this.from = from
        this.functionAddress = functionAddress
        this.functionConfiguration = functionConfiguration
        this.to = to
    }

    String id() {
        id.orElseGet{
            def hashBase = "${tenant}_${from}_${functionAddress}_${functionConfiguration}_${to.orElseGet{''}}"
            sha1Hex(hashBase).substring(0, 8)
        }
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
