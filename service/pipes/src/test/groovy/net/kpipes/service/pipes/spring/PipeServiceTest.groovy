package net.kpipes.service.pipes.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.http.CaseInsensitiveHeaders
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import net.kpipes.lib.testing.KPipesTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.core.KPipesFactory.kpipes
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
@Configuration
class PipeServiceTest extends KPipesTest {

    @Test(timeout = 30000L)
    void shouldListAddedPipes(TestContext context) {
        def async = context.async()
        kpipes = kpipes()
        kpipes.start()
        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])
        client.websocket(8080, "localhost", "/operation", headers) { websocket ->
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'pipe', operation: 'create', arguments: ['foo | functionFoo | bar']])))
            websocket.handler {
                def response = new ObjectMapper().readValue(it.bytes, Map)
                if(response.response == null) {
                    Thread.sleep(1000)
                    websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'pipe', operation: 'list'])))
                } else {
                    assertThat(response.response as List).contains('foo | functionFoo | bar')
                    async.complete()
                }
            }
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

}
