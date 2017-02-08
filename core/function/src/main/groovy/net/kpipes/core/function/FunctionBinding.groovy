/**
 * Licensed to the KPipes under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.function.spi.Function
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.slf4j.Logger

import static net.kpipes.core.event.EventDto.eventToDto
import static org.slf4j.LoggerFactory.getLogger

@CompileStatic
class FunctionBinding {

    private static final Logger LOG = getLogger(FunctionBinding)

    private final KPipes kpipes

    private final String address

    private final Function function

    FunctionBinding(KPipes kpipes, String address, Function function) {
        this.kpipes = kpipes
        this.address = address
        this.function = function
    }

    static FunctionBinding functionBinding(KPipes kpipes, String address, Function function) {
        new FunctionBinding(kpipes, address, function)
    }

    // Life-cycle

    FunctionBinding start() {
        LOG.debug('Starting function binding on address {}.', address)
        def kafkaPort = kpipes.configurationResolver().integer('kafka.port', 9092)
        def responseProducer = new KafkaProducerBuilder().port(kafkaPort).build()
        def functionConsumer = new KafkaConsumerBuilder<String, Bytes>('function.' + address).port(kafkaPort).build()
        kpipes.service(KafkaConsumerTemplate).get().subscribe(functionConsumer, "function.${address}") {
            def result = function.apply(new EventSerializer().deserialize(it.value().get()))
            if (result.target().present) {
                def payload = new Bytes(new ObjectMapper().writeValueAsBytes(eventToDto(result)))
                responseProducer.send(new ProducerRecord<String, Bytes>(result.target().get(), result.entityId().orElseGet {
                    UUID.randomUUID().toString()
                }, payload))
            }
        }
        this
    }

}