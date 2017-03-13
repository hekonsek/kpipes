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

import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

import static com.google.common.base.MoreObjects.firstNonNull
import static io.vertx.core.Vertx.vertx
import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.lib.commons.Uuids.uuid

class WebSocketsAdapter {

    private final KafkaConsumerTemplate kafkaConsumerTemplate

    private final KafkaProducer kafkaProducer

    private final BrokerAdmin brokerAdmin

    private final Authenticator authenticator

    // Configuration members

    private final int kafkaPort

    // Constructors

    WebSocketsAdapter(KafkaConsumerTemplate kafkaConsumerTemplate, KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin, Authenticator authenticator, int kafkaPort) {
        this.kafkaConsumerTemplate = kafkaConsumerTemplate
        this.kafkaProducer = kafkaProducer
        this.brokerAdmin = brokerAdmin
        this.authenticator = authenticator
        this.kafkaPort = kafkaPort
    }

    // Life-cycle

    void start() {
        vertx().createHttpServer().websocketHandler { socket ->
            def authentication = authenticator.authenticate(socket.headers().collectEntries())
            if(!authentication.present) {
                socket.reject()
            }

            def uri = socket.uri()
            if(uri.startsWith('/event/')) {
                def eventName = uri.replaceFirst(/\/event\//, '')
                brokerAdmin.ensureTopicExists(eventName)
                socket.handler { message ->
                    def key = socket.headers().get('key')
                    def topic = "${authentication.get().tenant}.${eventName}"
                    kafkaProducer.send(new ProducerRecord(topic, key ?: uuid(), new Bytes(message.bytes)))
                }
            } else if(uri.startsWith('/notification/')) {
                def channelName = uri.replaceFirst(/\/notification\//, '')
                def channel = "${authentication.get().tenant()}.notification.${channelName}"
                def historyMode = firstNonNull(socket.headers().get('history'), 'latest')
                if(historyMode == 'all') {
                    historyMode = 'earliest'
                }
                def kafkaConsumer = new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).offsetReset(historyMode).build()
                kafkaConsumerTemplate.subscribe(kafkaConsumer, channel) {
                    socket.write(buffer((it.value() as Bytes).get()))
                }
                socket.closeHandler {
                    kafkaConsumer.wakeup()
                }
            } else {
                socket.reject()
            }
        }.listen(8080)
    }

}
