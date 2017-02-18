package net.kpipes.functions.notification.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.lib.commons.Uuids
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Test
import org.junit.runner.RunWith

import static net.kpipes.core.KPipesFactory.kpipes

@RunWith(VertxUnitRunner)
class NotificationFunctionTest {

    static kpipesTest = new KPipesTest().start()

    static kafkaPort = kpipesTest.kafkaPort()

    static brokerAdmin = new BrokerAdmin('localhost', kpipesTest.zooKeeperPort())

    def source = Uuids.uuid()

    def target = Uuids.uuid()

    @Test(timeout = 30000L)
    void shouldRouteEvent(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = kpipes()
        def pipeBuilder = kpipes.pipeBuilder()
        pipeBuilder.build("${source} | notification [channel: '${target}']")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'tenant|user|foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(Uuids.uuid()).port(kafkaPort).build(), "tenant.notification.${target}") {
            async.complete()
        }
    }

}