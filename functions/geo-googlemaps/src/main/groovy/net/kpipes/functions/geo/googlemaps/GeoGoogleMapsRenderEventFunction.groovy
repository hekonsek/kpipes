package net.kpipes.functions.geo.googlemaps

import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import net.kpipes.lib.geo.Point

import static net.kpipes.lib.geo.GoogleMaps.renderCircleFenceMapUrl

class GeoGoogleMapsRenderEventFunction implements EventMappingFunction {

    @Override
    Map<String, Object> onEvent(Event event) {
        def fenceConfig = event.config().fence
        def fenceCenter = new Point(fenceConfig.center.lat as double, fenceConfig.center.lng as double)
        def marker = new Point(event.body().lat as double, event.body().lng as double)

        def mapUrl = renderCircleFenceMapUrl(fenceCenter, fenceConfig.radius as double, marker)
        event.body()['response.geo.googlemaps.render'] = [mapUrl: mapUrl]
        event.body()
    }

}