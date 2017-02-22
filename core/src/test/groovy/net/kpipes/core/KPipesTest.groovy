package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static net.kpipes.core.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
@Configuration
class KPipesTest {

    static kpipesTest = new net.kpipes.lib.testing.KPipesTest().start()

    static kafkaPort = kpipesTest.kafkaPort()

    static brokerAdmin = new BrokerAdmin('localhost', kpipesTest.zooKeeperPort())

    def source = uuid()

    def target = uuid()

    @Test(timeout = 60000L)
    void shouldExecuteFunction(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = kpipes()
        def pipeBuilder = kpipes.pipeBuilder()
        pipeBuilder.build("${source} | functionFoo | ${target}")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
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
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), 'finalTarget') {
            async.complete()
        }
    }

    @Test(timeout = 60000L)
    void shouldSplitStream(TestContext context) {
        // Given
        def secondTarget = uuid()
        def async = context.async()
        def kpipes = kpipes()
        def pipeBuilder = kpipes.pipeBuilder()
        pipeBuilder.build("${source} | functionFoo | ${target}")
        pipeBuilder.build("${source} | functionFoo | ${secondTarget}")

        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), secondTarget) {
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
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            def response = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            assertThat(response.config.configKey).isEqualTo('configValue')
            async.complete()
        }
    }

    @Test(timeout = 60000L)
    void shouldFilterOutMessage(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = kpipes()
        def pipeBuilder = kpipes.pipeBuilder()
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

    @Test(timeout = 60000L)
    void shouldRouteEvent(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = kpipes()
        def pipeBuilder = kpipes.pipeBuilder()
        pipeBuilder.build("${source} | routingFunction")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([target: target]))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            async.complete()
        }
    }

    @Bean
    EventMappingFunction functionFoo() {
        new EventMappingFunction() {
            @Override
            Map<String, Object> onEvent(Event event) {
                event.body().hello = 'world'
                event.body()
            }
        }
    }

    @Bean
    EventMappingFunction functionWithConfig() {
        new EventMappingFunction() {
            @Override
            Map<String, Object> onEvent(Event event) {
                event.body().config = event.config()
                event.body()
            }
        }
    }

    @Bean
    RoutingEventFunction routingFunction() {
        new RoutingEventFunction() {
            @Override
            RoutingEventFunction.RoutedEvent apply(Map<String, Object> config, String key, Map<String, Object> event) {
                new RoutingEventFunction.RoutedEvent(event, event.target as String)
            }
        }
    }

}