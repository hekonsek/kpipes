package net.kpipes.function.geo.geocoding.spring

import net.kpipes.core.function.Event
import net.kpipes.function.geo.geocoding.GeoCodingReverseFunction
import org.apache.camel.ProducerTemplate
import org.junit.Test

import static net.kpipes.core.spring.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

class GeoCodingTest {

    @Test
    void shouldExecuteReverseGeocoding() {
        System.setProperty('kafka.broker.enabled', 'false')
        System.setProperty('applicationId', uuid())
        System.setProperty('nodeId', uuid())
        def producerTemplate = kpipes().serviceRegistry().service(ProducerTemplate)
        def reverseFunction = new GeoCodingReverseFunction(producerTemplate)
        def result = reverseFunction.onEvent(new Event('tenant.topic', 'key', [lat: 40.714224, lng: -73.961452], [:], null))

        def response = result['response.geo.coding.reverse'] as Map<String, String>
        assertThat(response.address).contains('Brooklyn')
        assertThat(response.city).isEqualTo('New York')
    }

}
