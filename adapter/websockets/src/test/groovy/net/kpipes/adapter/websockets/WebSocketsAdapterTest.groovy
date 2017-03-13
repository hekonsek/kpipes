package net.kpipes.adapter.websockets

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.http.CaseInsensitiveHeaders
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.KPipes
import net.kpipes.lib.kafka.broker.TestBroker
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.core.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
@Configuration
class WebSocketsAdapterTest extends KPipesTest {

    @Before
    void before() {
        kpipes = kpipes()
    }

    @Test(timeout = 30000L)
    void shouldSendEvent(TestContext context) {
        // Given
        def async = context.async()
        kpipes.start()

        // When
        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])
        client.websocket(8080, "localhost", "/event/${source}", headers) { websocket ->
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([foo: 'bar'])))
        }

        // Then
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), 'anonymous.' + source) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            assertThat(event.foo)isEqualTo('bar')
            async.complete()
        }
    }

    @Test(timeout = 30000L)
    void shouldReceiveAllNotifications(TestContext context) {
        // Given
        def async = context.async()
        kpipes.start()
        brokerAdmin.ensureTopicExists("notification.${source}")

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord("anonymous.notification.${source}", uuid(), new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous', history: 'all'])
        client.websocket(8080, "localhost", "/notification/${source}", headers) { websocket ->
            websocket.handler {
                def event = new ObjectMapper().readValue(it.bytes, Map)
                assertThat(event.foo)isEqualTo('bar')
                async.complete()
            }
        }
    }

    @Test(timeout = 30000L)
    void shouldReceiveOnlyLatestNotificationByDefault(TestContext context) {
        // Given
        def async = context.async()
        kpipes.start()
        brokerAdmin.ensureTopicExists("notification.${source}")

        // When
        new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord("anonymous.notification.${source}", uuid(), new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'oldValue']))))

        // Then
        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])
        client.websocket(8080, "localhost", "/notification/${source}", headers) { websocket ->
            websocket.handler {
                def event = new ObjectMapper().readValue(it.bytes, Map)
                assertThat(event.foo)isEqualTo('bar')
                async.complete()
            }
            Thread.sleep(1000)
            new KafkaProducerBuilder<>().port(kafkaPort).build().send(new ProducerRecord("anonymous.notification.${source}", uuid(), new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))
        }
    }

    @Test(timeout = 30000L)
    void shouldInvokeOperation(TestContext context) {
        // Given
        def async = context.async()
        kpipes.start()

        // When
        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])
        client.websocket(8080, "localhost", "/operation", headers) { websocket ->
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'MyService', operation: 'echo', arguments: ['hello world']])))
            websocket.handler {
                def response = new ObjectMapper().readValue(it.bytes, Map)
                assertThat(response.response as String).isEqualTo('hello world')
                async.complete()
            }
        }
    }

    @Component('MyService')
    static class MyService {

        String echo(String text) {
            text
        }

    }

}
