package net.kpipes.core.event

import groovy.transform.CompileStatic

@CompileStatic
class Event {

    private final Map<String, Object> metaData

    private final Map<String, Object> headers

    private final Object body

    Event(Map<String, Object> metaData, Map<String, Object> headers, Object body) {
        this.metaData = metaData
        this.headers = headers
        this.body = body
    }

    Map<String, Object> metaData() {
        metaData
    }

    String user() {
        metaData.user
    }

    String tenant() {
        metaData.tenant
    }

    Optional<String> target() {
        Optional.ofNullable((String) metaData.target)
    }

    Optional<String> entityId() {
        Optional.ofNullable((String) metaData.entityId)
    }

    Map<String, Object> headers() {
        headers
    }

    Object body() {
        body
    }

}