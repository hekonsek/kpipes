package net.kpipes.functions.geo.geofencing

import net.kpipes.core.event.Event
import net.kpipes.core.function.spi.Function
import net.kpipes.lib.geo.Point

import static net.kpipes.lib.geo.Geofencing.metersOutsideFence

class GeoFencingDistanceFunction implements Function {

    @Override
    Event apply(Event event) {
        def fenceConfig = event.metaData().config.fence
        def fenceCenter = new Point(fenceConfig.center.lat as double, fenceConfig.center.lng as double)
        def fenceRadius = fenceConfig.radius as double

        def currentLocation = new Point(event.body().lat as double, event.body().lng as double)

        event.metaData()['response.geo.fencing.distance'] = [distance: metersOutsideFence(fenceCenter, currentLocation, fenceRadius)]
        event
    }

}