package net.kpipes.functions.geo.geocoding

import net.kpipes.core.spring.SpringFunctionRegistry
import org.apache.camel.ProducerTemplate
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class GeoCodingTest {

    @Test
    void shouldExecuteReverseGeocoding() {
        def producerTemplate = new SpringFunctionRegistry().applicationContext.getBean(ProducerTemplate)
        def reverseFunction = new GeoCodingReverseFunction(producerTemplate)
        def result = reverseFunction.apply([:], 'key', [lat: 40.714224, lng: -73.961452])

        def response = result['response.geo.coding.reverse'] as Map<String, String>
        assertThat(response.address).contains('Brooklyn')
        assertThat(response.city).isEqualTo('New York')
    }

}
