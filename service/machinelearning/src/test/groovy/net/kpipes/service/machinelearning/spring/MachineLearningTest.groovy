package net.kpipes.service.machinelearning.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import io.vertx.core.Vertx
import io.vertx.core.http.CaseInsensitiveHeaders
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.KPipes
import net.kpipes.core.KPipesConfig
import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import net.kpipes.lib.commons.Uuids
import net.kpipes.lib.testing.KPipesTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.core.spring.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class MachineLearningTest extends KPipesTest {

    int httpPort = availableTcpPort()

    @Override
    protected beforeKPipesCreated() {
        System.setProperty('http.port', httpPort + '')
    }

    @Test(timeout = 30000L)
    void shouldTrainModel(TestContext context) {
        def async = context.async()
        def config =  kpipes.serviceRegistry().service(KPipesConfig)
        new File(config.applicationHome(), '/store/fileSystemKeyValue/anonymous.dataset1').mkdirs()
        Files.copy(new File('src/test/resources/featureFector1.json'), new File(config.applicationHome(), '/store/fileSystemKeyValue/anonymous.dataset1/data1.json'))
        Files.copy(new File('src/test/resources/featureFector2.json'), new File(config.applicationHome(), '/store/fileSystemKeyValue/anonymous.dataset1/data2.json'))

        def client = Vertx.vertx().createHttpClient()
        def headers = new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])
        client.websocket(httpPort, "localhost", "/operation", headers) { websocket ->
            websocket.writeBinaryMessage(buffer(new ObjectMapper().writeValueAsBytes([service: 'machinelearning', operation: 'train', arguments: ['dataset1', 'mymodel']])))
            websocket.handler {
                assertThat(new File(config.applicationHome(), 'model/anonymous_mymodel').list().toList()).contains('model.pkl')
                async.complete()
            }
        }
    }

}
