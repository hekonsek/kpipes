package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.lib.commons.Uuids
import net.kpipes.lib.kafka.broker.KafkaBrokerFactory
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static net.kpipes.core.KPipesFactory.kpipes
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
@Configuration
class KPipesTest {

    def source = Uuids.uuid()

    def target = Uuids.uuid()

    @BeforeClass
    static void beforeClass() {
        new KafkaBrokerFactory().start()
    }

    @Test(timeout = 60000L)
    void shouldExecuteFunction(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = kpipes()
        def pipeBuilder = kpipes.pipeBuilder()
        pipeBuilder.build("${source} | functionFoo | ${target}")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(null).subscribe(new KafkaConsumerBuilder<>(Uuids.uuid()).build(), target) {
            async.complete()
        }
    }

    @Test(timeout = 60000L)
    void shouldConnectTwoPipes(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = kpipes()
        def pipeBuilder = kpipes.pipeBuilder()
        pipeBuilder.build("${source} | functionFoo | ${target}")
        pipeBuilder.build("${target} | functionFoo | finalTarget")

        kpipes.start()

        // When
        new KafkaProducerBuilder<>().build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(null).subscribe(new KafkaConsumerBuilder<>(Uuids.uuid()).build(), 'finalTarget') {
            async.complete()
        }
    }

    @Test(timeout = 60000L)
    void shouldSplitStream(TestContext context) {
        // Given
        def secondTarget = Uuids.uuid()
        def async = context.async()
        def kpipes = kpipes()
        def pipeBuilder = kpipes.pipeBuilder()
        pipeBuilder.build("${source} | functionFoo | ${target}")
        pipeBuilder.build("${source} | functionFoo | ${secondTarget}")

        kpipes.start()

        // When
        new KafkaProducerBuilder<>().build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(null).subscribe(new KafkaConsumerBuilder<>(Uuids.uuid()).build(), target) {
            new CachedThreadPoolKafkaConsumerTemplate(null).subscribe(new KafkaConsumerBuilder<>(Uuids.uuid()).build(), secondTarget) {
                async.complete()
            }
        }
    }

    @Test(timeout = 60000L)
    void shouldPassConfigToFunction(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = kpipes()
        def pipeBuilder = kpipes.pipeBuilder()
        pipeBuilder.build("${source} | functionWithConfig [configKey: 'configValue'] | ${target}")

        kpipes.start()

        // When
        new KafkaProducerBuilder<>().build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(null).subscribe(new KafkaConsumerBuilder<>(Uuids.uuid()).build(), target) {
            def response = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            assertThat(response.config.configKey).isEqualTo('configValue')
            async.complete()
        }
    }

        @Bean
        Function functionFoo() {
            new Function() {
                @Override
                Map<String, Object> apply(Map<String, Object> config, String key, Map<String, Object> event) {
                    event.hello = 'world'
                    event
                }
            }
        }

        @Bean
        Function functionWithConfig() {
            new Function() {
                @Override
                Map<String, Object> apply(Map<String, Object> config, String key, Map<String, Object> event) {
                    event.config = config
                    event
                }
            }
        }

}