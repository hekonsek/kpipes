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
package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import net.kpipes.lib.commons.Uuids
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.kstream.Predicate
import org.slf4j.Logger

import static net.kpipes.core.PipeDefinition.parsePipeDefinition
import static org.slf4j.LoggerFactory.getLogger

@CompileStatic
class PipeBuilder {

    private static final Logger LOG = getLogger(PipeBuilder)

    // Collaborators

    private final FunctionRegistry functionRegistry

    private final List<FunctionBuilder> functionBuilders

    // Members

    private final KPipesConfig config

    // Internal state

    private final Set<String> topics = new HashSet<>()

    private KStreamBuilder builder = new KStreamBuilder()

    private Map<String, KStream> sourceStreams = new HashMap<>()

    // Constructor

    PipeBuilder(KPipesConfig config, FunctionRegistry functionRegistry) {
        this.config = config
        this.functionRegistry = functionRegistry

        def brokerAdmin = functionRegistry.service(BrokerAdmin)
        def producer = functionRegistry.service(KafkaProducer)
        functionBuilders = [new EventFunctionBuilder(), new RoutingEventFunctionBuilder(producer, brokerAdmin), new EventStreamFunctionBuilder()] as List<FunctionBuilder>
    }

    // Operations

    void build(String pipeDefinition) {
        build(parsePipeDefinition(pipeDefinition))
    }

    void build(PipeDefinition pipeDefinition) {
        topics << pipeDefinition.from()
        if(pipeDefinition.to().isPresent()) {
            topics << pipeDefinition.to().get()
        }

        LOG.debug('Ensuring that all topics involved in a pipe exist.')

        def sourceStream = sourceStreams[pipeDefinition.from()]
        if (sourceStream == null) {
            sourceStream = builder.stream(pipeDefinition.from())
            sourceStreams[pipeDefinition.from()] = sourceStream
        }

        def function = functionRegistry.service(pipeDefinition.functionAddress())
        def functionBuilder = functionBuilders.find{ it.supports(function) }
        functionBuilder.build(pipeDefinition, function, sourceStream)
    }

    void start() {
        new BrokerAdmin(config.zooKeeperHost, config.zooKeeperPort).ensureTopicExists(topics)

        def streamsConfiguration = new Properties()
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-lambda-example" + Uuids.uuid());
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "${config.kafkaHost}:${config.kafkaPort}" as String);
        streamsConfiguration.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "${config.zooKeeperHost}:${config.zooKeeperPort}" as String);
        streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.Bytes().getClass().getName());
        new KafkaStreams(builder, streamsConfiguration).start()


    }

}