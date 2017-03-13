package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import net.kpipes.core.function.EventRoutingFunction
import net.kpipes.lib.kafka.broker.TestBroker
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
class KPipesTest {

    static kpipesTest = new TestBroker().start()

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
    void shouldExecuteFunction(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build("${source} | functionFoo | ${target}")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            async.complete()
        }
    }

    @Test(timeout = 30000L)
    void shouldConnectTwoPipes(TestContext context) {
        // Given
        def async = context.async()
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

    @Test(timeout = 30000L)
    void shouldSplitStream(TestContext context) {
        // Given
        def secondTarget = uuid()
        def async = context.async()
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

    @Test(timeout = 30000L)
    void shouldPassConfigToFunction(TestContext context) {
        // Given
        def async = context.async()
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

    @Test(timeout = 30000L)
    void shouldRouteEvent(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build("${source} | routingFunction")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([target: target]))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            async.complete()
        }
    }

    @Test(timeout = 30000L)
    void shouldExecuteJoinFunction(TestContext context) {
        // Given
        def async = context.async()
        def users = uuid()
        def orders = uuid()
        pipeBuilder.build("${orders} | join [onEvent: '${users}', onProperty: 'user'] | ${target}")
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(users, 'user1', new Bytes(new ObjectMapper().writeValueAsBytes([name: 'john']))))
        kpipes.start()

        // When
        Thread.sleep(5000)
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(orders, 'orderKey', new Bytes(new ObjectMapper().writeValueAsBytes([orderid: 100, user: 'user1']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            def event = kpipes.serviceRegistry().service(EventEncoder).decode(it.value() as Bytes)
            assertThat(event.user as Map).containsEntry('name', 'john')
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
    EventRoutingFunction routingFunction() {
        new EventRoutingFunction(){
            @Override
            Optional<String> onEvent(Event event) {
                Optional.of(event.body().target as String)
            }
        }
    }

}