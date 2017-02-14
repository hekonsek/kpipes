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
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.kstream.KeyValueMapper
import org.slf4j.Logger

import static org.slf4j.LoggerFactory.getLogger

@CompileStatic
class PipeBuilder {

    KStreamBuilder builder = new KStreamBuilder()

    Map<String, KStream> streams = new HashMap<>()

    Set<String> topic = new HashSet<>()

    private static final Logger LOG = getLogger(PipeBuilder)

    // Collaborators

    private final KPipesConfig config

    private final FunctionRegistry functionRegistry

    // Constructor

    PipeBuilder(KPipesConfig config, FunctionRegistry functionRegistry) {
        this.config = config
        this.functionRegistry = functionRegistry
    }

    // Operations

    void build(String pipeDefinition) {
        build(PipeDefinition.parsePipeDefinition(pipeDefinition))
    }

    void build(PipeDefinition pipeDefinition) {
//        LOG.debug('Building pipe from definition: {}', pipeDefinition)

        topic << pipeDefinition.from()
        topic << pipeDefinition.to()

        LOG.debug('Ensuring that all topics involved in a pipe exist.')

        def function = functionRegistry.service(pipeDefinition.functionAddress())
        def st = streams[pipeDefinition.from()]
        if (st == null) {
            st = builder.stream(pipeDefinition.from())
            streams[pipeDefinition.from()] = st
        }
        st.map(new KeyValueMapper<String, Bytes, KeyValue>() {
            @Override
            KeyValue apply(String key, Bytes value) {
                def event = new ObjectMapper().readValue(value.get(), Map)
                new KeyValue<>(key, new Bytes(new ObjectMapper().writeValueAsBytes(function.apply(pipeDefinition.functionConfiguration(), key, event))))
            }
        }).to(pipeDefinition.to())
    }

    void start() {
        new BrokerAdmin(config.zooKeeperHost, config.zooKeeperPort).ensureTopicExists(topic)

        def streamsConfiguration = new Properties()
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-lambda-example" + Uuids.uuid());
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "${config.kafkaHost}:${config.kafkaPort}" as String);
        streamsConfiguration.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "${config.zooKeeperHost}:${config.zooKeeperPort}" as String);
        streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.Bytes().getClass().getName());
        new KafkaStreams(builder, streamsConfiguration).start()


    }

}