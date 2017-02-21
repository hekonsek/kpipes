package net.kpipes.functions.notification.spring

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.EventEncoder
import net.kpipes.lib.commons.Uuids
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.Test
import org.junit.runner.RunWith

import static net.kpipes.core.KPipesFactory.kpipes

@RunWith(VertxUnitRunner)
class NotificationFunctionTest {

    static kpipesTest = new KPipesTest().start()

    static kafkaPort = kpipesTest.kafkaPort()

    def source = Uuids.uuid()

    def target = Uuids.uuid()

    @Test(timeout = 60000L)
    void shouldRouteEvent(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = kpipes()
        def brokerAdmin = kpipes.serviceRegistry().service(BrokerAdmin)
        def kafkaProducer = kpipes.serviceRegistry().service(KafkaProducer)
        def encoder = kpipes.serviceRegistry().service(EventEncoder)
        def pipeBuilder = kpipes.pipeBuilder()
        pipeBuilder.build("${source} | notification [channel: '${target}']")
        kpipes.start()

        // When
        kafkaProducer.send(new ProducerRecord(source, 'tenant|user|foo', encoder.encode([foo: 'bar'])))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(Uuids.uuid()).port(kafkaPort).build(), "tenant.notification.${target}") {
            async.complete()
        }
    }

}