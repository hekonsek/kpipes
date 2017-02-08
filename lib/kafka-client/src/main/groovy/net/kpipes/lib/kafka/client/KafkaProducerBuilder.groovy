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

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.BytesSerializer
import org.apache.kafka.common.serialization.Serializer

class KafkaProducerBuilder<K,V> {

    private String host = 'localhost'

    private int port = 9092

    private int retries = 5

    private Class<Serializer<V>> valueSerializer = BytesSerializer

    KafkaProducer<K,V> build(Properties additionalProperties) {
        def config = new Properties()
        config.put('bootstrap.servers', "${host}:${port}" as String)
        config.put('retries', retries)
        config.put('acks', 'all')
        config.put("linger.ms", 1);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        config.put('value.serializer', valueSerializer.name)
        config.putAll(additionalProperties)
        new KafkaProducer<K,V>(config)
    }

    KafkaProducer<K,V> build() {
        build(new Properties())
    }

    KafkaProducerBuilder<K,V> host(String host) {
        this.host = host
        this
    }

    KafkaProducerBuilder<K,V> port(int port) {
        this.port = port
        this
    }

    KafkaProducerBuilder<K,V> retries(int retries) {
        this.retries = retries
        this
    }

    KafkaProducerBuilder<K,V> valueSerializer(Class<Serializer<V>> valueSerializer) {
        this.valueSerializer = valueSerializer
        this
    }
}