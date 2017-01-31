package net.kpipes.core.event

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic

import static net.kpipes.core.event.EventDto.eventToDto

@CompileStatic
class EventSerializer {

    private final ObjectMapper json = new ObjectMapper()

    byte[] serialize(Event event) {
        json.writeValueAsBytes(eventToDto(event))
    }

    Event deserialize(byte[] eventBytes) {
        json.readValue(eventBytes, EventDto).toEvent()
    }

}
