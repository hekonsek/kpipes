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
package net.kpipes.lib.kafka.client

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.BytesDeserializer
import org.apache.kafka.common.serialization.Serializer

class KafkaConsumerBuilder<K,V> {

    private final String consumerGroup

    private String host = 'localhost'

    private int port = 9092

    private Class<Serializer<V>> valueSerializer = BytesDeserializer

    KafkaConsumerBuilder(String consumerGroup) {
        this.consumerGroup = consumerGroup
    }

    KafkaConsumer<K,V> build(Properties additionalProperties) {
        def config = new Properties()
        config.put('bootstrap.servers', "${host}:${port}" as String)
        config.put('group.id', consumerGroup)
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        config.put('value.deserializer', valueSerializer.name)
        config.put('enable.auto.commit', "false")
        config.put("auto.offset.reset", "earliest")
        config.putAll(additionalProperties)
        new KafkaConsumer<K,V>(config)
    }

    KafkaConsumer<K,V> build() {
        build(new Properties())
    }

    KafkaConsumerBuilder<K,V> host(String host) {
        this.host = host
        this
    }

    KafkaConsumerBuilder<K,V> port(int port) {
        this.port = port
        this
    }

    KafkaConsumerBuilder<K,V> valueSerializer(Class<Serializer<V>> valueSerializer) {
        this.valueSerializer = valueSerializer
        this
    }
}