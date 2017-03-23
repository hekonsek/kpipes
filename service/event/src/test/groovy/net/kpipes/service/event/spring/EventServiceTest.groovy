package net.kpipes.service.event.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import io.vertx.core.Vertx
import io.vertx.core.http.CaseInsensitiveHeaders
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.common.utils.Bytes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.core.spring.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class EventServiceTest extends KPipesTest {

    def home = Files.createTempDir()

    @Before
    void before() {
        System.setProperty('kipes.home', home.absolutePath)
    }

    @Test(timeout = 30000L)
    void shouldAddEvent(TestContext context) {
        // Given
        int httpPort = availableTcpPort()
        System.setProperty('http.port', httpPort + '')
        def async = context.async()
        kpipes = kpipes()
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
            kpipes.stop()
            async.complete()
        }
    }

    @Test(timeout = 30000L)
    void shouldListEvents(TestContext context) {
        // Given
        int httpPort = availableTcpPort()
        System.setProperty('http.port', httpPort + '')
        def async = context.async()
        kpipes = kpipes()
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
                    kpipes.stop()
                    async.complete()
                }
            }
        }
    }

    @Test(timeout = 30000L)
    void shouldCountEvents(TestContext context) {
        // Given
        int httpPort = availableTcpPort()
        System.setProperty('http.port', httpPort + '')
        def async = context.async()
        kpipes = kpipes()
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
                    kpipes.stop()
                    async.complete()
                }
            }
        }
    }

}
