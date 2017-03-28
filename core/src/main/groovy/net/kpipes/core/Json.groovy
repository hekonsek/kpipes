package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper

class Json {

    private final ObjectMapper mapper = new ObjectMapper()

    byte[] asBytes(Object object) {
        mapper.writeValueAsBytes(object)
    }

    Map<String, Object> read(byte[] bytes) {
        mapper.readValue(bytes, Map)
    }

}
