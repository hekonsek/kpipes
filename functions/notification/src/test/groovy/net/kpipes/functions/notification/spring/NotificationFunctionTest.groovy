package net.kpipes.functions.notification.spring

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.EventEncoder
import net.kpipes.lib.commons.Uuids
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.Test
import org.junit.runner.RunWith

import static net.kpipes.core.spring.KPipesFactory.kpipes

@RunWith(VertxUnitRunner)
class NotificationFunctionTest extends KPipesTest {

    @Test(timeout = 30000L)
    void shouldRouteEvent(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = kpipes()
        def brokerAdmin = kpipes.serviceRegistry().service(BrokerAdmin)
        def encoder = kpipes.serviceRegistry().service(EventEncoder)
        def pipeBuilder = kpipes.pipeBuilder()
        pipeBuilder.build(tenant, "${source} | notification [channel: '${target}']")
        kpipes.startPipes()

        // When
        kafkaProducer.send(new ProducerRecord(effectiveSource, 'foo', encoder.encode([foo: 'bar'])))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(Uuids.uuid()).port(kafkaPort).build(), "${tenant}.notification.${target}") {
            async.complete()
        }
    }

}