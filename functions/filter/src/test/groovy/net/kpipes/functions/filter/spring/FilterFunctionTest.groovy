package net.kpipes.functions.filter.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.EventEncoder
import net.kpipes.core.KPipes
import net.kpipes.core.PipeBuilder
import net.kpipes.core.RoutingEventFunction
import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static net.kpipes.core.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
@Configuration
class FilterFunctionTest {

    static kpipesTest = new net.kpipes.lib.testing.KPipesTest().start()

    static kafkaPort = kpipesTest.kafkaPort()

    KPipes kpipes

    BrokerAdmin brokerAdmin

    PipeBuilder pipeBuilder

    def source = uuid()

    def target = uuid()

    @Before
    void before() {
        kpipes = kpipes()
        brokerAdmin = kpipes.serviceRegistry().service(BrokerAdmin)
        pipeBuilder = kpipes.pipeBuilder()
    }

    // Tests

    @Test(timeout = 30000L)
    void shouldFilterOutMessage(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build("${source} | filter [predicate: 'event.foo == /baz/'] | ${target}")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'key', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'key', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'baz']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            assertThat(event.foo)isEqualTo('baz')
            async.complete()
        }
    }

}