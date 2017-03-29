package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import net.kpipes.core.function.EventRoutingFunction
import net.kpipes.core.repository.PipeDefinitionsRepository
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

import static com.google.common.io.Files.createTempDir
import static net.kpipes.core.PipeDefinitionEncoder.decodePipe
import static net.kpipes.core.spring.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
@Configuration
class KPipesTest {

    static kpipesTest = new TestBroker().start()

    static kafkaPort = kpipesTest.kafkaPort()

    def home = createTempDir()

    KPipes kpipes

    BrokerAdmin brokerAdmin

    PipeBuilder pipeBuilder

    def tenant = uuid()

    def source = uuid()

    def effectiveSource = "${tenant}.${source}" as String

    def target = uuid()

    def effectiveTarget = "${tenant}.${target}" as String

    @Before
    void before() {
        System.setProperty('kpipes.home', home.absolutePath)
        System.setProperty('applicationId', uuid())
        System.setProperty('nodeId', uuid())
        kpipes = kpipes()
        brokerAdmin = kpipes.serviceRegistry().service(BrokerAdmin)
        pipeBuilder = kpipes.pipeBuilder()
    }

    // Tests

    @Test(timeout = 30000L)
    void shouldExecuteFunction(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build(tenant, "${source} | functionFoo | ${target}")
        kpipes.startPipes()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(effectiveSource, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), effectiveTarget) {
            async.complete()
        }
    }

    @Test(timeout = 30000L)
    void shouldConnectTwoPipes(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build(tenant, "${source} | functionFoo | ${target}")
        pipeBuilder.build(tenant, "${target} | functionFoo | finalTarget")

        kpipes.startPipes()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(effectiveSource, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), "${tenant}.finalTarget") {
            async.complete()
        }
    }

    @Test(timeout = 30000L)
    void shouldSplitStream(TestContext context) {
        // Given
        def secondTarget = uuid()
        def async = context.async()
        pipeBuilder.build(tenant, "${source} | functionFoo | ${target}")
        pipeBuilder.build(tenant, "${source} | functionFoo | ${secondTarget}")
        kpipes.startPipes()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(effectiveSource, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), effectiveTarget) {
            new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), "${tenant}.${secondTarget}") {
                async.complete()
            }
        }
    }

    @Test(timeout = 30000L)
    void shouldPassConfigToFunction(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build(tenant, "${source} | functionWithConfig [configKey: 'configValue'] | ${target}")
        kpipes.startPipes()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(effectiveSource, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), effectiveTarget) {
            def response = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            assertThat(response.config.configKey).isEqualTo('configValue')
            async.complete()
        }
    }

    @Test(timeout = 30000L)
    void shouldRouteEvent(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build(tenant, "${source} | routingFunction")
        kpipes.startPipes()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(effectiveSource, 'foo', new Bytes(new ObjectMapper().writeValueAsBytes([target: effectiveTarget as String]))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), effectiveTarget) {
            async.complete()
        }
    }

    @Test(timeout = 30000L)
    void shouldExecuteJoinFunction(TestContext context) {
        // Given
        def async = context.async()
        def users = uuid()
        def orders = uuid()
        pipeBuilder.build(tenant, "${orders} | join [onEvent: '${users}', onProperty: 'user'] | ${target}")
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord("${tenant}.${users}", 'user1', new Bytes(new ObjectMapper().writeValueAsBytes([name: 'john']))))
        kpipes.startPipes()

        // When
        Thread.sleep(5000)
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord("${tenant}.${orders}", 'orderKey', new Bytes(new ObjectMapper().writeValueAsBytes([orderid: 100, user: 'user1']))))

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), effectiveTarget) {
            def event = kpipes.serviceRegistry().service(EventEncoder).decode(it.value() as Bytes)
            assertThat(event.user as Map).containsEntry('name', 'john')
            async.complete()
        }
    }

    @Test
    void shouldListDefinitions() {
        // Given
        kpipes.startPipes()
        kpipes.addPipe(decodePipe(tenant, "${source} | functionFoo | ${target}"))

        // When
        def definitions = kpipes.serviceRegistry().service(PipeDefinitionsRepository).list()

        // Then
        assertThat(definitions).hasSize(1)
        assertThat(definitions.first().effectiveFrom()).isEqualTo(effectiveSource)
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