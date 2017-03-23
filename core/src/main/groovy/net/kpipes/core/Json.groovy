package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper

class Json {

    private ObjectMapper mapper = new ObjectMapper()

    byte[] asBytes(Object object) {
        mapper.writeValueAsBytes(object)
    }

}
