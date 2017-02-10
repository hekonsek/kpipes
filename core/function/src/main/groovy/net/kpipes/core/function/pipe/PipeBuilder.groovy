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
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.utils.Bytes
import org.slf4j.Logger

import static net.kpipes.core.function.pipe.PipeDefinition.parsePipeDefinition
import static org.slf4j.LoggerFactory.getLogger

@CompileStatic
class PipeBuilder {

    private static final Logger LOG = getLogger(PipeBuilder)

    // Collaborators

    private final KPipes kpipes

    // Internal collaborators

    private FunctionExecutor functionExecutor

    // Constructor

    PipeBuilder(KPipes kpipes) {
        this.kpipes = kpipes
    }

    void start() {
        LOG.debug('Starting pipe builder.')
        this.functionExecutor = kpipes.service(FunctionExecutor).get()
    }

    // Operations

    void build(String pipeDefinition) {
        build(parsePipeDefinition(pipeDefinition))
    }

    void build(PipeDefinition pipeDefinition) {
        LOG.debug('Building pipe from definition: {}', pipeDefinition)

        LOG.debug('Ensuring that all topics involved in a pipe exist.')
        kpipes.service(BrokerAdmin).get().ensureTopicExists(pipeDefinition.from())
        kpipes.service(BrokerAdmin).get().ensureTopicExists(pipeDefinition.functionAddress())
        if (pipeDefinition.to().present) {
            kpipes.service(BrokerAdmin).get().ensureTopicExists(pipeDefinition.to().get())
        }

        def kafkaPort = kpipes.configurationResolver().integer('kafka.port', 9092)
        def consumer = new KafkaConsumerBuilder<String, Bytes>(pipeDefinition.id()).port(kafkaPort).build()
        kpipes.service(KafkaConsumerTemplate).get().subscribe(consumer, pipeDefinition.from()) { ConsumerRecord<String, Bytes> eventRecord ->
            def event = new EventSerializer().deserialize(eventRecord.value().get())
            if (!pipeDefinition.functionConfiguration().isEmpty()) {
                event.metaData().functionConfig = pipeDefinition.functionConfiguration()
            }
            if (pipeDefinition.to().present) {
                event.metaData().put('target', pipeDefinition.to().get())
            }
            functionExecutor.executeFunction("function.${pipeDefinition.functionAddress()}", eventRecord.key(), event)
        }
    }

}