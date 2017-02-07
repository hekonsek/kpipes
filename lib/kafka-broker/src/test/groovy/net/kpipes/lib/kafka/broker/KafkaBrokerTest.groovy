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
package net.kpipes.lib.kafka.broker

import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static org.assertj.core.api.Assertions.assertThat

class KafkaBrokerTest {

    @Test
    void shouldReadFromKafka() {
        def kafkaPort = availableTcpPort()
        System.setProperty('kafka.port', "${kafkaPort}")
        System.setProperty('kafka.dataDirectory', "${createTempDir().absolutePath}")
        System.setProperty('zooKeeper.port', "${availableTcpPort()}")
        System.setProperty('zooKeeper.dataDirectory', "${createTempDir().absolutePath}")
        new KPipes().start()

        def config = new Properties()
        config.put('bootstrap.servers', "localhost:${kafkaPort}".toString())
        config.put('group.id', 'groupName')
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        config.put('enable.auto.commit', "false")
        config.put("auto.offset.reset", "earliest")
        def consumer = new KafkaConsumer<>(config)

        new KafkaProducerBuilder().port(kafkaPort).valueSerializer(StringSerializer).build().
                send(new ProducerRecord('foo', 'key', 'bar'))

        consumer.subscribe(["foo".toString()])
        while(true) {
            def events = consumer.poll(5000).iterator()
            while (events.hasNext()) {
                consumer.commitSync()
                assertThat(events.next().value()).isEqualTo('bar')
                return
            }
            Thread.sleep(100)
        }
    }

}
