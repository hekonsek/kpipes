package net.kpipes.service.kpipes.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.http.CaseInsensitiveHeaders
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.lib.testing.KPipesTest
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.core.spring.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Mavens.artifactVersionFromDependenciesProperties
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static org.assertj.core.api.Assertions.assertThat

@Ignore
@RunWith(VertxUnitRunner)
class KpipesServiceTest extends KPipesTest {

    @Test(timeout = 30000L)
    void shouldReturnKPipesVersion(TestContext context) {
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
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'kpipes', operation: 'version'])))

            // Then
            websocket.handler {
                def response = new ObjectMapper().readValue(it.bytes, Map)
                assertThat(response.response as String).isEqualTo(artifactVersionFromDependenciesProperties('net.kpipes', 'kpipes-lib-commons').get())
                async.complete()
            }
        }
    }

}
