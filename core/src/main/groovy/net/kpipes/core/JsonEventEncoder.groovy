package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.utils.Bytes

class JsonEventEncoder implements EventEncoder {

    private final json = new ObjectMapper()

    @Override
    Bytes encode(Map<String, Object> event) {
        new Bytes(json.writeValueAsBytes(event))
    }

    @Override
    Map<String, Object> decode(Bytes event) {
        json.readValue(event.get(), Map)
    }

}
