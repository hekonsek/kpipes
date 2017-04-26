package net.kpipes.executor.spring

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.function.Event
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

import static net.kpipes.lib.commons.Uuids.uuid

@RunWith(VertxUnitRunner)
@Configuration
class ServiceEventPipelineTest extends KPipesTest {

    @Test(timeout = 30000L)
    void shouldExecuteServiceRegistryFunction(TestContext context) {
        def async = context.async()
        brokerAdmin.ensureTopicExists('tenant.service.request.endpointId', 'tenant.service.response.endpointId')
        send('tenant.service.request.endpointId', uuid(), [service: 'echo', operation: 'echo', body: [hello: 'world!']])

        def consumer = new KafkaConsumerBuilder(uuid()).port(kafkaPort).build()
        kpipes.serviceRegistry().service(KafkaConsumerTemplate).subscribe(consumer, 'tenant.service.response.endpointId') {
            async.complete()
        }
    }

    @Component('echo')
    static class EchoService {

        Map<String, Object> echo(Event event) {
            event.body()
        }

    }

}
