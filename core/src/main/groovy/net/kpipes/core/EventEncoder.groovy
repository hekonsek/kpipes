package net.kpipes.core

import org.apache.kafka.common.utils.Bytes

interface EventEncoder {

    Bytes encode(Map<String, Object> event)

    Map<String, Object> decode(Bytes event)

}
