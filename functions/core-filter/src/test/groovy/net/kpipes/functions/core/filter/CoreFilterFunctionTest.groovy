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
package net.kpipes.functions.core.filter

import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.function.FunctionBinding
import net.kpipes.core.function.pipe.PipeBuilder
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Ignore
import org.junit.Test

import java.util.concurrent.Callable

import static com.google.common.io.Files.createTempDir
import static com.jayway.awaitility.Awaitility.await
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@Ignore
class CoreFilterFunctionTest {

    @Test
    void pipeShouldFilterOutEvent() {
        def kafkaPort = availableTcpPort()
        System.setProperty('kafka.port', "${kafkaPort}")
        System.setProperty('kafka.dataDirectory', "${createTempDir().absolutePath}")
        System.setProperty('zooKeeper.port', "${availableTcpPort()}")
        System.setProperty('zooKeeper.dataDirectory', "${createTempDir().absolutePath}")
        def kpipes = new KPipes().start()
        def serializer = new EventSerializer()
        new FunctionBinding(kpipes, 'hello.world', { (it.body() as Map).hello = (it.body() as Map).name; it }).start()
        kpipes.service(PipeBuilder).build('source | core.filter [predicate: "event.body.name == /henry2/"] | results')

        // When
        def producer = new KafkaProducerBuilder().port(kafkaPort).build()
        producer.send(new ProducerRecord('source', 'key', new Bytes(serializer.serialize(new Event([:], [:], [name: 'henry1'])))))
        producer.send(new ProducerRecord('source', 'key', new Bytes(serializer.serialize(new Event([:], [:], [name: 'henry2'])))))

        // Then
        def resultsConsumer = new KafkaConsumerBuilder(uuid()).port(kafkaPort).build()
        await().until({ resultsConsumer.partitionsFor('results').size() > 0 } as Callable<Boolean>)
        resultsConsumer.subscribe(['results'])
        while(true) {
            def events = resultsConsumer.poll(5000).iterator()
            while (events.hasNext()) {
                resultsConsumer.commitSync()
                def bytes = events.next().value() as Bytes
                def event = serializer.deserialize(bytes.get())
                assertThat((event.body() as Map).name).isEqualTo('henry2')
                return
            }
            Thread.sleep(100)
        }
    }

}
