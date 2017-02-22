package net.kpipes.functions.geo.geocoding

import com.google.code.geocoder.model.GeocodeResponse
import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import org.apache.camel.ProducerTemplate

class GeoCodingReverseEventFunction implements EventMappingFunction {

    private final ProducerTemplate camelProducerTemplate

    GeoCodingReverseEventFunction(ProducerTemplate camelProducerTemplate) {
        this.camelProducerTemplate = camelProducerTemplate
    }

    @Override
    Map<String, Object> onEvent(Event event) {
        def geocodeResponse = camelProducerTemplate.requestBody("geocoder:latlng:${event.body().lat},${event.body().lng}", null, GeocodeResponse)
        def address = geocodeResponse.results.first().formattedAddress
        def city = geocodeResponse.results.find { it.types.contains('locality') }.addressComponents.first().longName
        event.body()['response.geo.coding.reverse'] = [address: address, city: city]
        event.body()
    }

}