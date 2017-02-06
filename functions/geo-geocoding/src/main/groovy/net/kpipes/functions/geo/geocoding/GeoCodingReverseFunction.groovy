package net.kpipes.functions.geo.geocoding

import com.google.code.geocoder.model.GeocodeResponse
import net.kpipes.core.event.Event
import net.kpipes.core.function.spi.Function
import org.apache.camel.ProducerTemplate

class GeoCodingReverseFunction implements Function {

    private final ProducerTemplate camelProducerTemplate

    GeoCodingReverseFunction(ProducerTemplate camelProducerTemplate) {
        this.camelProducerTemplate = camelProducerTemplate
    }

    @Override
    Event apply(Event event) {
        def body = event.body() as Map
        def geocodeResponse = camelProducerTemplate.requestBody("geocoder:latlng:${body.lat},${body.lng}", null, GeocodeResponse)
        def address = geocodeResponse.results.first().formattedAddress
        def city = geocodeResponse.results.find{ it.types.contains('locality') }.addressComponents.first().longName
        event.metaData()['response.geo.coding.reverse'] = [address: address, city: city]
        event
    }

}