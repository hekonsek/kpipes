package net.kpipes.core.event

import groovy.transform.CompileStatic

@CompileStatic
class EventDto {

    Map<String, Object> metaData

    Map<String, Object> headers

    Object body

    static EventDto eventToDto(Event event) {
        new EventDto(metaData: event.metaData(), headers: event.headers(), body: event.body())
    }

    Event toEvent() {
        new Event(metaData, headers, body)
    }

}