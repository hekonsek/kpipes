package net.kpipes.function.geo.geocoding

import com.google.code.geocoder.model.GeocodeResponse
import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import org.apache.camel.ProducerTemplate

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeoutException

import static java.util.concurrent.TimeUnit.SECONDS

class GeoCodingReverseFunction implements EventMappingFunction {

    private final executor = Executors.newCachedThreadPool()

    private final ProducerTemplate camelProducerTemplate

    GeoCodingReverseFunction(ProducerTemplate camelProducerTemplate) {
        this.camelProducerTemplate = camelProducerTemplate
    }

    @Override
    Map<String, Object> onEvent(Event event) {
        try {
            def geocodeResponse = executor.submit({
                camelProducerTemplate.requestBody("geocoder:latlng:${event.body().lat},${event.body().lng}", null, GeocodeResponse)
            } as Callable<GeocodeResponse>).get(5, SECONDS)
            def address = geocodeResponse.results.first().formattedAddress
            def city = geocodeResponse.results.find { it.types.contains('locality') }.addressComponents.first().longName
            event.body()['response.geo.coding.reverse'] = [address: address, city: city]
        } catch (TimeoutException e) {
            event.body()['response.geo.coding.reverse'] = [error: 'Connection timeout']
        }
        event.body()
    }

}