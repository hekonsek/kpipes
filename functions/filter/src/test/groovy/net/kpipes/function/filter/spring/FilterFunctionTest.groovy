package net.kpipes.function.filter.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.PipeBuilder
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static net.kpipes.core.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class FilterFunctionTest extends KPipesTest {

    PipeBuilder pipeBuilder

    @Before
    void before() {
        kpipes = kpipes()
        pipeBuilder = kpipes.pipeBuilder()
    }

    // Tests

    @Test(timeout = 30000L)
    void shouldFilterOutMessage(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build(tenant, "${source} | filter [predicate: 'event.foo == /baz/'] | ${target}")
        kpipes.start()

        // When
        kafkaProducer.send(new ProducerRecord(effectiveSource, 'key', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))
        kafkaProducer.send(new ProducerRecord(effectiveSource, 'key', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'baz']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), effectiveTarget) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            assertThat(event.foo)isEqualTo('baz')
            async.complete()
        }
    }

}