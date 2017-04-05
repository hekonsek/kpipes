package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.utils.Bytes

class Json {

    private final ObjectMapper mapper = new ObjectMapper()

    byte[] asBytesArray(Object object) {
        mapper.writeValueAsBytes(object)
    }

    Bytes asBytes(Object object) {
        new Bytes(asBytesArray(object))
    }

    Bytes asBytes(Map<String, Object> object) {
        asBytes((Object) object)
    }

    Map<String, Object> read(byte[] bytes) {
        mapper.readValue(bytes, Map)
    }

}
