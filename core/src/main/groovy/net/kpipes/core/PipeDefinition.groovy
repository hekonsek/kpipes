package net.kpipes.core

import groovy.transform.CompileStatic
import groovy.transform.ToString

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex

@CompileStatic
@ToString(includeFields = true)
class PipeDefinition {

    private final String tenant

    private final Optional<String> id

    private final String from

    private final String functionAddress

    private final Map<String, Object> functionConfiguration

    private final Optional<String> to

    // Constructors

    PipeDefinition(String tenant, Optional<String> id, String from, String functionAddress, Map<String, Object> functionConfiguration, Optional<String> to) {
        this.tenant = tenant
        this.id = id
        this.from = from
        this.functionAddress = functionAddress
        this.functionConfiguration = functionConfiguration
        this.to = to
    }

    // Accessors

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
        functionAddress
    }

    Map<String, Object> functionConfiguration() {
        functionConfiguration
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
