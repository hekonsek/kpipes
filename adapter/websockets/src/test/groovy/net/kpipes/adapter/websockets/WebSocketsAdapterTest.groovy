package net.kpipes.adapter.websockets

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.KPipesFactory
import net.kpipes.lib.kafka.broker.KafkaBrokerFactory
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.Configuration

import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
@Configuration
class WebSocketsAdapterTest {

    def source = uuid()

    def target = uuid()

    @BeforeClass
    static void beforeClass() {
        new KafkaBrokerFactory().start()
    }

    @Test(timeout = 60000L)
    void shouldSendEvent(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = KPipesFactory.kpipes()
        kpipes.start()

        // When
        def client = Vertx.vertx().createHttpClient()
        client.websocket(8080, "localhost", "/event/${source}") { websocket ->
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([foo: 'bar'])))
        }

        // Then
        new BrokerAdmin('localhost', 2181).ensureTopicExists(source)
        new CachedThreadPoolKafkaConsumerTemplate(null).subscribe(new KafkaConsumerBuilder<>(uuid()).build(), source) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            assertThat(event.foo)isEqualTo('bar')
            async.complete()
        }
    }

    @Test(timeout = 60000L)
    void shouldReceiveNotification(TestContext context) {
        // Given
        def async = context.async()
        def kpipes = KPipesFactory.kpipes()
        kpipes.start()
        new BrokerAdmin('localhost', 2181).ensureTopicExists("notification.${source}")

        // When
        new KafkaProducerBuilder<>().build().send(new ProducerRecord("notification.${source}", uuid(), new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'bar']))))

        // Then
        def client = Vertx.vertx().createHttpClient()
        client.websocket(8080, "localhost", "/notification/${source}") { websocket ->
            websocket.handler {
                def event = new ObjectMapper().readValue(it.bytes, Map)
                assertThat(event.foo)isEqualTo('bar')
                async.complete()
            }
        }
    }

}
