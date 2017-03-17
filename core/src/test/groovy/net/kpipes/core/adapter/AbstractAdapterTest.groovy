package net.kpipes.core.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipesContext
import net.kpipes.core.ServiceRegistry
import org.junit.Before
import org.junit.Test
import org.springframework.stereotype.Component

import static net.kpipes.core.KPipesContext.registerContext
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

class AbstractAdapterTest {

    def application = uuid()

    def adapter = new AbstractAdapter(new KPipesContext(application)){}

    @Before
    void before() {
        registerContext(application, new ServiceRegistry() {
            @Override
            Object service(String id) {
                new EchoService()
            }

            @Override
            def <T> T service(Class<T> type) {
                null
            }

            @Override
            def <T> List<T> services(Class<T> type) {
                []
            }
        }, null)
    }

    @Test
    void shouldInvokeOperation() {
        // Given
        def request = [service: 'echo', operation: 'echo', arguments: ['foo']]

        // When
        def encodedResponse = adapter.invokeOperation('tenant', new ObjectMapper().writeValueAsBytes(request))
        def response = new ObjectMapper().readValue(encodedResponse, Map).response

        // Then
        assertThat(response).isEqualTo('foo')
    }

    static class EchoService {

        String echo(String message) {
            message
        }

    }

}
