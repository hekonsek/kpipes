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
package net.kpipes.adapter.websockets

import io.vertx.core.Vertx
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.lib.commons.Uuids.uuid

class WebSocketsAdapter {

    private final KafkaConsumerTemplate kafkaConsumerTemplate

    private final KafkaProducer kafkaProducer

    private final BrokerAdmin brokerAdmin

    private final int kafkaPort

    WebSocketsAdapter(KafkaConsumerTemplate kafkaConsumerTemplate, KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin, int kafkaPort) {
        this.kafkaConsumerTemplate = kafkaConsumerTemplate
        this.kafkaProducer = kafkaProducer
        this.brokerAdmin = brokerAdmin
        this.kafkaPort = kafkaPort
    }

    // Life-cycle

    void start() {
        Vertx.vertx().createHttpServer().websocketHandler { ws ->
            def uri = ws.uri()
            if(uri.startsWith('/event/')) {
                def eventName = uri.replaceFirst(/\/event\//, '')
                brokerAdmin.ensureTopicExists(eventName)
                ws.handler { message ->
                    def key = ws.headers().get('kpipes.key')
                    kafkaProducer.send(new ProducerRecord(eventName, key ?: uuid(), new Bytes(message.bytes)))
                }
            } else if(uri.startsWith('/notification/')) {
                def channelName = uri.replaceFirst(/\/notification\//, '')
                def channel = "notification.${channelName}"
                brokerAdmin.ensureTopicExists(channel) // TODO kafkaConsumerTemplate.subscribe should create topic
                kafkaConsumerTemplate.subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), channel) {
                    ws.write(buffer((it.value() as Bytes).get()))
                }
                ws.closeHandler {
                    // stop kafka consumer
                }
            } else {
                ws.reject()
            }
        }.listen(8080)
    }

}
