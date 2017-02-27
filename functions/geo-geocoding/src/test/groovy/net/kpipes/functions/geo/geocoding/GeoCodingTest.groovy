package net.kpipes.functions.geo.geocoding

import net.kpipes.core.function.Event
import net.kpipes.core.spring.SpringServiceRegistry
import org.apache.camel.ProducerTemplate
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class GeoCodingTest {

    @Test
    void shouldExecuteReverseGeocoding() {
        def producerTemplate = new SpringServiceRegistry().applicationContext.getBean(ProducerTemplate)
        def reverseFunction = new GeoCodingReverseEventFunction(producerTemplate)
        def result = reverseFunction.onEvent(new Event('key', [lat: 40.714224, lng: -73.961452], [:], null))

        def response = result['response.geo.coding.reverse'] as Map<String, String>
        assertThat(response.address).contains('Brooklyn')
        assertThat(response.city).isEqualTo('New York')
    }

}
