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
package net.kpipes.core.function.pipe

import groovy.transform.CompileStatic
import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

@CompileStatic
class KafkaFunctionExecutor implements FunctionExecutor {

    // Collaborators

    private final KPipes kpipes

    // Internal collaborators

    private KafkaProducer<String, Bytes> responseProducer

    // Constructors

    KafkaFunctionExecutor(KPipes kpipes) {
        this.kpipes = kpipes
    }

    // Life-cycle

    void start() {
        def kafkaPort = kpipes.configurationResolver().integer('kafka.port', 9092)
        responseProducer = new KafkaProducerBuilder().port(kafkaPort).build()
    }

    // Operations

    @Override
    void executeFunction(String functionAddress, String eventKey, Event event) {
        responseProducer.send(new ProducerRecord(functionAddress, eventKey, new Bytes(new EventSerializer().serialize(event))))
    }

}