package net.kpipes.functions.geo.geocoding

import com.google.code.geocoder.model.GeocodeResponse
import net.kpipes.core.EventFunction
import org.apache.camel.ProducerTemplate

class GeoCodingReverseEventFunction implements EventFunction {

    private final ProducerTemplate camelProducerTemplate

    GeoCodingReverseEventFunction(ProducerTemplate camelProducerTemplate) {
        this.camelProducerTemplate = camelProducerTemplate
    }

    @Override
    Map<String, Object> apply(Map<String, Object> config, String key, Map<String, Object> event) {
        def geocodeResponse = camelProducerTemplate.requestBody("geocoder:latlng:${event.lat},${event.lng}", null, GeocodeResponse)
        def address = geocodeResponse.results.first().formattedAddress
        def city = geocodeResponse.results.find { it.types.contains('locality') }.addressComponents.first().longName
        event['response.geo.coding.reverse'] = [address: address, city: city]
        event
    }

}