package net.kpipes.functions.geo.googlemaps

import net.kpipes.core.Function
import net.kpipes.lib.geo.Point

import static net.kpipes.lib.geo.GoogleMaps.renderCircleFenceMapUrl

class GeoGoogleMapsRenderFunction implements Function {

    @Override
    Map<String, Object> apply(Map<String, Object> config, String key, Map<String, Object> event) {
        def fenceConfig = config.fence
        def fenceCenter = new Point(fenceConfig.center.lat as double, fenceConfig.center.lng as double)
        def marker = new Point(event.lat as double, event.lng as double)

        def mapUrl = renderCircleFenceMapUrl(fenceCenter, fenceConfig.radius as double, marker)
        event['response.geo.googlemaps.render'] = [mapUrl: mapUrl]
        event
    }

}