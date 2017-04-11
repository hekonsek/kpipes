package net.kpipes.service.event.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.http.CaseInsensitiveHeaders
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.common.utils.Bytes
import org.junit.Test
import org.junit.runner.RunWith

import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class EventServiceTest extends KPipesTest {

    int httpPort = availableTcpPort()

    @Override
    protected beforeKPipesCreated() {
        System.setProperty('http.port', httpPort + '')
    }

    @Test(timeout = 30000L)
    void shouldAddEvent(TestContext context) {
        // Given
        def async = context.async()
        kpipes.startPipes()
        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])

        client.websocket(httpPort, "localhost", "/operation", headers) { websocket ->
            // When
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'event', operation: 'add', arguments: [source, null, [foo: 'bar']]])))
        }

        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), "anonymous.${source}") {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            assertThat(event.foo)isEqualTo('bar')
            async.complete()
        }
    }

    @Test(timeout = 30000L)
    void shouldListEvents(TestContext context) {
        // Given
        def async = context.async()
        kpipes.pipeBuilder().build('anonymous', "${source} | view.materialize.keyvalue")
        kpipes.startPipes()
        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])

        client.websocket(httpPort, "localhost", "/operation", headers) { websocket ->
            // When
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'event', operation: 'add', arguments: [source, 'key', [foo: 'bar']]])))
            Thread.sleep(5000)
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'event', operation: 'view', arguments: [source]])))
            websocket.handler {
                def response = new ObjectMapper().readValue(it.bytes, Map).response
                if(response instanceof Map) {
                    assertThat(response).isEqualTo([key: [foo: 'bar']])
                    kpipes.stopPipes()
                    async.complete()
                }
            }
        }
    }

    @Test(timeout = 30000L)
    void shouldCountEvents(TestContext context) {
        // Given
        def async = context.async()
        kpipes.pipeBuilder().build('anonymous', "${source} | view.materialize.keyvalue")
        kpipes.startPipes()
        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])

        client.websocket(httpPort, "localhost", "/operation", headers) { websocket ->
            // When
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'event', operation: 'add', arguments: [source, 'key', [foo: 'bar']]])))
            Thread.sleep(6000)
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'event', operation: 'count', arguments: [source]])))
            websocket.handler {
                def response = new ObjectMapper().readValue(it.bytes, Map).response
                if(response == 1) {
                    kpipes.stopPipes()
                    async.complete()
                }
            }
        }
    }

    @Test(timeout = 30000L)
    void shouldNotCountNotExistingCollection(TestContext context) {
        // Given
        def async = context.async()
        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])

        client.websocket(httpPort, "localhost", "/operation", headers) { websocket ->
            // When
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'event', operation: 'count', arguments: [uuid()]])))
            websocket.handler {
                def response = new ObjectMapper().readValue(it.bytes, Map).response as String
                assertThat(response).contains("doesn't exist")
                async.complete()
            }
        }
    }

    @Test(timeout = 30000L)
    void shouldListTopics(TestContext context) {
        // Given
        def async = context.async()
        def topic = uuid()
        brokerAdmin.ensureTopicExists("anonymous.${topic}")
        kpipes.startPipes()
        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])

        client.websocket(httpPort, "localhost", "/operation", headers) { websocket ->
            websocket.handler {
                def event = new ObjectMapper().readValue(it.bytes, Map)
                assertThat(event.response as List).contains(topic)
                async.complete()
            }
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'event', operation: 'list'])))
        }
    }

}
