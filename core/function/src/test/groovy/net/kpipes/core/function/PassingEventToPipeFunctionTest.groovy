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
import kafka.admin.AdminUtils
import kafka.admin.RackAwareMode
import kafka.utils.ZKStringSerializer$
import kafka.utils.ZkUtils
import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.function.pipe.PipeBuilder
import net.kpipes.core.starter.service.InMemoryServiceRegistry
import net.kpipes.lib.commons.Uuids
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.I0Itec.zkclient.ZkClient
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.utils.Bytes
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class PassingEventToPipeFunctionTest {

    @Test(timeout = 60000L)
    void shouldPassEventToFunctionTopic(TestContext context) {
        def async = context.async()
        def kpipesTest = new KPipesTest().start()
        def kpipes = kpipesTest.kpipes()
        def kafkaPort = kpipesTest.kafkaPort()

        def zkClient = new ZkClient('localhost:' + kpipes.configurationResolver().integer('zooKeeper.port', 2181), Integer.MAX_VALUE, 10000, ZKStringSerializer$.MODULE$)
        if (!AdminUtils.topicExists(ZkUtils.apply(zkClient, false), 'source')) {
            RackAwareMode mode = RackAwareMode.Disabled$.MODULE$
            AdminUtils.createTopic(ZkUtils.apply(zkClient, false), 'source', 25, 1, new Properties(), mode)
            Thread.sleep(10000)
        }


        def serializer = new EventSerializer()
        kpipes.service(PipeBuilder).get().build('source | hello.world | results')

        // When
        def producer = new KafkaProducerBuilder().port(kafkaPort).build()
        producer.send(new ProducerRecord('source', 'key', new Bytes(serializer.serialize(new Event([:], [:], [name: 'henry'])))))

        // Then
        if (!AdminUtils.topicExists(ZkUtils.apply(zkClient, false), 'function.hello.world')) {
            RackAwareMode mode = RackAwareMode.Disabled$.MODULE$
            AdminUtils.createTopic(ZkUtils.apply(zkClient, false), 'function.hello.world', 25, 1, new Properties(), mode)
            Thread.sleep(10000)
        }

        def consumer = new KafkaConsumerBuilder(Uuids.uuid()).port(kafkaPort).build()
        consumer.subscribe(['function.hello.world'], new ConsumerRebalanceListener() {
            @Override
            void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                println 'REVOKED'
                println AdminUtils.topicExists(ZkUtils.apply(new ZkClient('localhost:' + kpipes.configurationResolver().integer('zooKeeper.port', 2181)), true), 'function.hello.world')
                println consumer.partitionsFor('function.hello.world').size()
                println partitions
            }

            @Override
            void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                println 'ASSIGNED'
                println AdminUtils.topicExists(ZkUtils.apply(new ZkClient('localhost:' + kpipes.configurationResolver().integer('zooKeeper.port', 2181)), true), 'function.hello.world')
                println consumer.partitionsFor('function.hello.world').size()
                println partitions
            }
        })
        kpipes.service(KafkaConsumerTemplate).get().consumeRecord(consumer) {
            async.complete()
        }
    }

}
