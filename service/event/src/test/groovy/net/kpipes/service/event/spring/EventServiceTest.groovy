package net.kpipes.service.event.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.http.CaseInsensitiveHeaders
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.lib.commons.Uuids
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.utils.Bytes
import org.junit.Test
import org.junit.runner.RunWith

import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.core.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Mavens.artifactVersionFromDependenciesProperties
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class EventServiceTest extends KPipesTest {

    @Test(timeout = 30000L)
    void shouldAddEvent(TestContext context) {
        // Given
        int httpPort = availableTcpPort()
        System.setProperty('http.port', httpPort + '')
        def async = context.async()
        kpipes = kpipes()
        kpipes.start()
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

}
