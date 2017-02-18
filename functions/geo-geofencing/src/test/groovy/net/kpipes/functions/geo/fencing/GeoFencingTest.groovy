package net.kpipes.functions.geo.fencing

import net.kpipes.functions.geo.geofencing.GeoFencingDistanceEventFunction
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class GeoFencingTest {

    @Test
    void shouldCalculateDistanceFromFence() {
        def function = new GeoFencingDistanceEventFunction()
        def fenceConfig = [fence: [center: [lat: 49.820813, lng: 19.054982], radius: 25]]

        // When
        def result = function.apply(fenceConfig, 'key', [lat: 49.820829, lng: 19.056378])

        // Then
        def response = result['response.geo.fencing.distance']
        assertThat(response.distance as double).isGreaterThan(75d)
    }

}
