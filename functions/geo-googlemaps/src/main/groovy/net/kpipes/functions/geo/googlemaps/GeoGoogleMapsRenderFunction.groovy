package net.kpipes.functions.geo.googlemaps

import net.kpipes.core.event.Event
import net.kpipes.core.function.spi.Function
import net.kpipes.lib.geo.Point

import static net.kpipes.lib.geo.GoogleMaps.renderCircleFenceMapUrl

class GeoGoogleMapsRenderFunction implements Function {

    @Override
    Event apply(Event event) {
        def config = event.metaData().config
        def fenceConfig = config.fence
        def fenceCenter = new Point(fenceConfig.center.lat as double, fenceConfig.center.lng as double)
        def marker = new Point(event.body.lat as double, event.body.lng as double)

        def mapUrl = renderCircleFenceMapUrl(fenceCenter, fenceConfig.radius as double, marker)
        event.metaData()['response.geo.googlemaps.render'] = [mapUrl: mapUrl]
        event
    }

}