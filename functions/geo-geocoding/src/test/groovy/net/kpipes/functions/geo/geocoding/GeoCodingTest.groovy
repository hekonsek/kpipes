package net.kpipes.functions.geo.geocoding

import net.kpipes.core.event.Event
import org.apache.camel.impl.DefaultCamelContext
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class GeoCodingTest {

    @Test
    void shouldExecuteReverseGeocoding() {
        def camel = new DefaultCamelContext()
        camel.start()
        def reverseFunction = new GeoCodingReverseFunction(camel.createProducerTemplate())
        def result = reverseFunction.apply(new Event([:], [:], [lat: 40.714224, lng: -73.961452]))

        def response = result.metaData()['response.geo.coding.reverse'] as Map<String, String>
        assertThat(response.address).contains('Brooklyn')
        assertThat(response.city).isEqualTo('New York')
    }

}
