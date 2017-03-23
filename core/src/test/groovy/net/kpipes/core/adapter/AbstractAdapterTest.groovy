package net.kpipes.core.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.spring.KPipesFactory
import org.junit.Before
import org.junit.Test
import org.springframework.stereotype.Component

import static org.assertj.core.api.Assertions.assertThat

class AbstractAdapterTest {

    AbstractAdapter adapter

    @Before
    void before() {
        System.setProperty('kafka.broker.enabled', 'false')
        adapter = new AbstractAdapter(KPipesFactory.kpipes()){}
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

    @Component('echo')
    static class EchoService {

        String echo(String message) {
            message
        }

    }

}
