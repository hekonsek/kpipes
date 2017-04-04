package net.kpipes.core.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.Json
import net.kpipes.core.KPipes
import org.junit.Test
import org.springframework.stereotype.Component

import static com.google.common.io.Files.createTempDir
import static net.kpipes.core.spring.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

class AbstractAdapterTest {

    static KPipes kpipes

    static AbstractAdapter adapter

    static Json json

    static {
        System.setProperty('kafka.broker.enabled', 'false')
        System.setProperty('kpipes.home', createTempDir().absolutePath)
        System.setProperty('applicationId', uuid())
        System.setProperty('nodeId', uuid())
        kpipes = kpipes()
        adapter = new AbstractAdapter(kpipes) {}
        json = kpipes.serviceRegistry().service(Json)
    }

    @Test
    void shouldInvokeOperation() {
        // Given
        def request = [service: 'echo', operation: 'echo', arguments: ['foo']]

        // When
        def encodedResponse = adapter.invokeOperation('tenant', json.asBytes(request))
        def response = new ObjectMapper().readValue(encodedResponse, Map).response

        // Then
        assertThat(response).isEqualTo('foo')
    }

    @Test
    void shouldInvokeVoidOperation() {
        // Given
        def request = [service: 'echo', operation: 'voidOperation']

        // When
        def encodedResponse = adapter.invokeOperation('tenant', json.asBytes(request))
        def response = new ObjectMapper().readValue(encodedResponse, Map).response

        // Then
        assertThat(response).isNull()
        assertThat(EchoService.voidOperationExecuted).isEqualTo(true)
    }

    @Component('echo')
    static class EchoService {

        static voidOperationExecuted = false

        String echo(String message) {
            message
        }

        void voidOperation() {
            voidOperationExecuted = true
        }

    }

}
