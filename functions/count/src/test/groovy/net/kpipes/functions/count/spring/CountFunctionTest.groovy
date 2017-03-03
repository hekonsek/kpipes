package net.kpipes.functions.count.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.KPipes
import net.kpipes.core.PipeBuilder
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
import org.springframework.context.annotation.Configuration

import static net.kpipes.core.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid

@RunWith(VertxUnitRunner)
@Configuration
class CountFunctionTest {

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

    @Test(timeout = 90000L)
    void shouldCountEvents(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build("${source} | count [groupBy: 'country', byTenant: false] | ${target}")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, uuid(), new Bytes(new ObjectMapper().writeValueAsBytes([country: 'PL']))))
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, uuid(), new Bytes(new ObjectMapper().writeValueAsBytes([country: 'PL']))))
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, uuid(), new Bytes(new ObjectMapper().writeValueAsBytes([country: 'US']))))

        // Then
        def results = [:]
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            results[it.key] = event.count
            if(results.US == 1 && results.PL == 2) {
                async.complete()
                kpipes.pipeBuilder().stop()
            }
        }
    }

    @Test(timeout = 90000L)
    void shouldCountEventsByTenant(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build("${source} | count [groupBy: 'country'] | ${target}")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, "tenant1|user|${uuid()}" as String, new Bytes(new ObjectMapper().writeValueAsBytes([country: 'PL']))))
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, "tenant2|user|${uuid()}" as String, new Bytes(new ObjectMapper().writeValueAsBytes([country: 'PL']))))
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, "tenant2|user|${uuid()}" as String, new Bytes(new ObjectMapper().writeValueAsBytes([country: 'PL']))))
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, "tenant1|user|${uuid()}" as String, new Bytes(new ObjectMapper().writeValueAsBytes([country: 'US']))))

        // Then
        def results = [:]
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            results[it.key()] = event.count
            if(results['tenant1|US'] == 1 && results['tenant1|PL'] == 1 && results['tenant2|PL'] == 2) {
                async.complete()
                kpipes.pipeBuilder().stop()
            }
        }
    }

    @Test(timeout = 90000L)
    void shouldCompactEvents(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build("${source} | count [groupBy: 'country', byTenant: false] | ${target}")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'key', new Bytes(new ObjectMapper().writeValueAsBytes([country: 'PL']))))
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'key', new Bytes(new ObjectMapper().writeValueAsBytes([country: 'US']))))

        // Then
        def results = [:]
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            results[it.key] = event.count
            if(results.US == 1) {
                async.complete()
                kpipes.pipeBuilder().stop()
            }
        }
    }

    @Test(timeout = 90000L)
    void shouldSubtractOnEventRemoval(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build("${source} | count [groupBy: 'country', byTenant: false] | ${target}")
        kpipes.start()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, uuid(), new Bytes(new ObjectMapper().writeValueAsBytes([country: 'US']))))
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'key', new Bytes(new ObjectMapper().writeValueAsBytes([country: 'US']))))

        // Then
        def results = [:]
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), target) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            results[it.key()] = event.count
            if(results.US == 2) {
                new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(source, 'key', null))
            }
            if(results.US == 1) {
                async.complete()
                kpipes.pipeBuilder().stop()
            }
        }
    }

}