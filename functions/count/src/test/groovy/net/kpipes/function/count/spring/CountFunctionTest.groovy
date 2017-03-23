package net.kpipes.function.count.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.PipeBuilder
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

import static net.kpipes.core.spring.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid

@RunWith(VertxUnitRunner)
@Ignore
class CountFunctionTest extends KPipesTest {

    PipeBuilder pipeBuilder

    @Before
    void before() {
        kpipes = kpipes()
        pipeBuilder = kpipes.pipeBuilder()
    }

    @Test(timeout = 30000L)
    void shouldCountEvents(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build(tenant, "${source} | count [groupBy: 'country'] | ${target}")
        kpipes.startPipes()

        // When
        kafkaProducer.send(new ProducerRecord(effectiveSource, "tenant1|user|${uuid()}" as String, new Bytes(json.writeValueAsBytes([country: 'PL']))))
        kafkaProducer.send(new ProducerRecord(effectiveSource, "tenant2|user|${uuid()}" as String, new Bytes(json.writeValueAsBytes([country: 'PL']))))
        kafkaProducer.send(new ProducerRecord(effectiveSource, "tenant2|user|${uuid()}" as String, new Bytes(json.writeValueAsBytes([country: 'PL']))))
        kafkaProducer.send(new ProducerRecord(effectiveSource, "tenant1|user|${uuid()}" as String, new Bytes(json.writeValueAsBytes([country: 'US']))))

        // Then
        def results = [:]
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), effectiveTarget) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            results[it.key()] = event.count
            if(results['US'] == 1 && results['PL'] == 3) {
                kpipes.stopPipes()
                async.complete()
            }
        }
    }

    @Test(timeout = 30000L)
    void shouldCompactEvents(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build(tenant, "${source} | count [groupBy: 'country'] | ${target}")
        kpipes.startPipes()

        // When
        send(effectiveSource, 'key', [country: 'PL'])
        send(effectiveSource, 'key', [country: 'US'])

        // Then
        def results = [:]
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), effectiveTarget) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            results[it.key] = event.count
            if(results.US == 1) {
                kpipes.stopPipes()
                async.complete()
            }
        }
    }

    @Test(timeout = 30000L)
    void shouldSubtractOnEventRemoval(TestContext context) {
        // Given
        def async = context.async()
        pipeBuilder.build(tenant, "${source} | count [groupBy: 'country'] | ${target}")
        kpipes.startPipes()

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(effectiveSource, uuid(), new Bytes(new ObjectMapper().writeValueAsBytes([country: 'US']))))
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(effectiveSource, 'key', new Bytes(new ObjectMapper().writeValueAsBytes([country: 'US']))))

        // Then
        def results = [:]
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), effectiveTarget) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            results[it.key()] = event.count
            if(results.US == 2) {
                new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord(effectiveSource, 'key', null))
            }
            if(results.US == 1) {
                kpipes.stopPipes()
                async.complete()
            }
        }
    }

}