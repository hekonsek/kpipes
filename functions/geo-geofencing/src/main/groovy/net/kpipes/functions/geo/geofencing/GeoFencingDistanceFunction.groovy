package net.kpipes.functions.geo.geofencing

import net.kpipes.core.EventFunction
import net.kpipes.lib.geo.Point

import static net.kpipes.lib.geo.Geofencing.metersOutsideCircle

class GeoFencingDistanceFunction implements EventFunction {

    @Override
    Map<String, Object> apply(Map<String, Object> config, String key, Map<String, Object> event) {
        def fenceConfig = config.fence as Map
        def fenceCenter = new Point(fenceConfig.center.lat as double, fenceConfig.center.lng as double)
        def fenceRadius = fenceConfig.radius as double

        def currentLocation = new Point(event.lat as double, event.lng as double)

        event['response.geo.fencing.distance'] = [distance: metersOutsideCircle(fenceCenter, currentLocation, fenceRadius)]
        event
    }

}