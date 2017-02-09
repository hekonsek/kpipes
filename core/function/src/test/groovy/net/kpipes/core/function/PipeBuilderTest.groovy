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
package net.kpipes.core.function

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.function.pipe.PipeBuilder
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import static net.kpipes.core.function.FunctionBinding.functionBinding
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class PipeBuilderTest {

    static def kpipesTest = new KPipesTest().start()

    static def kpipes = kpipesTest.kpipes()

    static def kafkaPort = kpipesTest.kafkaPort()

    @BeforeClass
    static void beforeClass() {
        functionBinding(kpipes, 'hello.world') { it.body().hello = it.body().name; it }.start()
    }

    @Test(timeout = 60000L)
    void pipeShouldInvokeFunction(TestContext context) {
        def async = context.async()
        def serializer = new EventSerializer()
        kpipes.service(PipeBuilder).get().build('source | hello.world | results')

        // When
        def producer = new KafkaProducerBuilder().port(kafkaPort).build()
        producer.send(new ProducerRecord('source', 'key', new Bytes(serializer.serialize(new Event([:], [:], [name: 'henry'])))))

        // Then
        def resultsConsumer = new KafkaConsumerBuilder<String, Bytes>(uuid()).port(kafkaPort).build()
        resultsConsumer.subscribe(['results'])
        kpipes.service(KafkaConsumerTemplate).get().consumeRecord(resultsConsumer) {
            def event = serializer.deserialize(it.value().get())
            assertThat(event.body().hello).isEqualTo('henry')
            async.complete()
        }
    }

    @Test(timeout = 60000L)
    void onePipeShouldProduceToAnother(TestContext context) {
        def async = context.async()
        def serializer = new EventSerializer()
        kpipes.service(PipeBuilder).get().build('source | hello.world | source2')
        kpipes.service(PipeBuilder).get().build('source2 | hello.world | results2')

        // When
        def producer = new KafkaProducerBuilder().port(kafkaPort).build()
        producer.send(new ProducerRecord('source', 'key', new Bytes(serializer.serialize(new Event([:], [:], [name: 'henry'])))))

        // Then
        def resultsConsumer = new KafkaConsumerBuilder<String, Bytes>(uuid()).port(kafkaPort).build()
        resultsConsumer.subscribe(['results2'])
        kpipes.service(KafkaConsumerTemplate).get().consumeRecord(resultsConsumer) {
            def event = serializer.deserialize(it.value().get())
            assertThat(event.body().hello).isEqualTo('henry')
            async.complete()
        }
    }

}
