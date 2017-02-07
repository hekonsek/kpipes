package net.kpipes.core.event

import groovy.transform.CompileStatic

@CompileStatic
class Event {

    private final Map<String, Object> metaData

    private final Map<String, Object> headers

    private final Map<String, Object> body

    Event(Map<String, Object> metaData, Map<String, Object> headers, Map<String, Object> body) {
        this.metaData = new LinkedHashMap<>(metaData)
        this.headers = new LinkedHashMap<>(headers)
        this.body = new LinkedHashMap<>(body)
    }

    Event(Map<String, Object> body) {
        this([:], [:], body)
    }

    Map<String, Object> metaData() {
        metaData
    }

    Optional<String> user() {
        Optional.ofNullable((String) metaData.user)
    }

    Optional<String> tenant() {
        Optional.ofNullable((String) metaData.tenant)
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

    Map<String, Object> body() {
        body
    }

}