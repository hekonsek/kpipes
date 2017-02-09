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

import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.function.pipe.FunctionExecutor
import net.kpipes.core.function.pipe.MockFunctionExecutor
import net.kpipes.core.function.pipe.PipeBuilder
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Before
import org.junit.Test

import java.util.concurrent.Callable

import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.MINUTES
import static net.kpipes.lib.commons.Uuids.uuid

class PipeSourceReadingTest {

    def kpipesTest = new KPipesTest(new MockFunctionExecutor()).start()

    def kpipes = kpipesTest.kpipes()

    def producer = kpipesTest.eventProducer()

    // Data fixtures

    def source = "source-${uuid()}"

    def key = "key-${uuid()}" as String

    // Topics fixtures

    @Before
    void before() {
        kpipes.service(BrokerAdmin).get().ensureTopicExists(source)
    }

    // Tests

    @Test
    void shouldPassEventToFunctionExecutor() {
        // Given
        def serializer = new EventSerializer()
        kpipes.service(PipeBuilder).get().build("${source} | function")

        // When
        producer.send(new ProducerRecord(source, key, new Bytes(serializer.serialize(new Event([:], [:], [name: 'henry'])))))

        // Then
        def exec = kpipes.service(FunctionExecutor).get() as MockFunctionExecutor
        await().until({!exec.events().empty} as Callable<Boolean>)
    }

    @Test
    void shouldPassSeriesOfEventsToFunctionExecutor() {
        // Given
        def serializer = new EventSerializer()
        kpipes.service(PipeBuilder).get().build("${source} | function")

        // When
        100.times {
            producer.send(new ProducerRecord(source, key, new Bytes(serializer.serialize(new Event([:], [:], [name: 'henry'])))))
        }

        // Then
        def exec = kpipes.service(FunctionExecutor).get() as MockFunctionExecutor
        await().atMost(1, MINUTES).until({exec.events().size() == 100} as Callable<Boolean>)
    }

}
