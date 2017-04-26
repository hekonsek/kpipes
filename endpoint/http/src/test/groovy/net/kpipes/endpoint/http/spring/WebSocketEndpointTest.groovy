package net.kpipes.endpoint.http.spring

import io.vertx.core.Vertx
import io.vertx.core.http.CaseInsensitiveHeaders
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.CountDownLatch
import java.util.regex.Pattern

import static io.vertx.core.buffer.Buffer.buffer
import static java.util.concurrent.TimeUnit.SECONDS
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class WebSocketEndpointTest extends KPipesTest {

    int httpPort = availableTcpPort()

    def client = Vertx.vertx().createHttpClient()

    @Override
    protected beforeKPipesCreated() {
        System.setProperty('http.port', httpPort + '')
    }

    @Test(timeout = 30000L)
    void shouldReceiveAllNotifications(TestContext context) {
        // Given
        def async = context.async()
        kpipes.startPipes()
        brokerAdmin.ensureTopicExists("notification.${source}")

        // When
        send("anonymous.notification.${source}", uuid(), [foo: 'bar'])

        // Then
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous', history: 'all'])
        client.websocket(httpPort, "localhost", "/notification/${source}", headers) { websocket ->
            websocket.handler {
                def event = json.read(it.bytes)
                assertThat(event.foo)isEqualTo('bar')
                async.complete()
            }
        }
    }

    @Test(timeout = 30000L)
    void shouldReceiveOnlyLatestNotificationByDefault(TestContext context) {
        // Given
        def async = context.async()
        kpipes.startPipes()
        brokerAdmin.ensureTopicExists("notification.${source}")

        // When
        send("anonymous.notification.${source}", uuid(), [foo: 'oldValue'])

        // Then
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])
        client.websocket(httpPort, "localhost", "/notification/${source}", headers) { websocket ->
            websocket.handler {
                def event = json.read(it.bytes)
                assertThat(event.foo)isEqualTo('bar')
                async.complete()
            }
            Thread.sleep(1000)
            send("anonymous.notification.${source}", uuid(), [foo: 'bar'])
        }
    }

    @Test(timeout = 30000L)
    void shouldInvokeOperation(TestContext context) {
        // Given
        def async = context.async()
        kpipes.startPipes()

        // When
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])
        client.websocket(httpPort, "localhost", "/service", headers) { websocket ->
            websocket.writeBinaryMessage(buffer(json.asBytesArray([service: 'MyService', operation: 'echo', arguments: ['hello world']])))
            websocket.handler {
                def response = json.read(it.bytes)
                assertThat(response.response as String).isEqualTo('hello world')
                async.complete()
            }
        }

        Thread.sleep(1000)
        def consumer = new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build()
        kpipes.serviceRegistry().service(KafkaConsumerTemplate).subscribe(consumer, Pattern.compile(/anonymous\.service\.request\..+/)) {
            send(it.topic().replaceFirst('request','response'), it.key() as String, [response: 'hello world'])
        }
    }

    @Test
    void shouldNotReturnResponseIfNoServiceIsPresent() {
        def semaphore = new CountDownLatch(1)
        kpipes.startPipes()

        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])
        client.websocket(httpPort, "localhost", "/service", headers) { websocket ->
            websocket.writeBinaryMessage(buffer(json.asBytesArray([service: 'MyService', operation: 'echo', arguments: ['hello world']])))
            websocket.handler {
                semaphore.countDown()
            }
        }
        assertThat(semaphore.await(10L, SECONDS)).isFalse()
    }

}