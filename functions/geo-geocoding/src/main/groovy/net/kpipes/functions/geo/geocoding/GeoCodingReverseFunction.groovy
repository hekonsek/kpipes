package net.kpipes.functions.geo.geocoding

import com.google.code.geocoder.model.GeocodeResponse
import net.kpipes.core.Function
import org.apache.camel.ProducerTemplate

class GeoCodingReverseFunction implements Function {

    private final ProducerTemplate camelProducerTemplate

    GeoCodingReverseFunction(ProducerTemplate camelProducerTemplate) {
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