package net.kpipes.functions.geo.geofencing

import net.kpipes.core.EventFunction
import net.kpipes.lib.geo.Point

import static net.kpipes.lib.geo.Geofencing.metersOutsideCircle
import static net.kpipes.lib.geo.Geofencing.metersOutsidePolygon
import static org.slf4j.LoggerFactory.getLogger

class GeoFencingDistanceFunction implements EventFunction {

    private static final LOG = getLogger(GeoFencingDistanceFunction)

    @Override
    Map<String, Object> apply(Map<String, Object> config, String key, Map<String, Object> event) {
        def currentLocation = new Point(event.lat as double, event.lng as double)
        LOG.debug('About to invoke function against location: {}', currentLocation)
        def fenceConfig = config.fence as Map

        if(fenceConfig.containsKey('center')) {
            def fenceCenter = new Point(fenceConfig.center.lat as double, fenceConfig.center.lng as double)
            def fenceRadius = fenceConfig.radius as double
            event['response.geo.fencing.distance'] = [distance: metersOutsideCircle(fenceCenter, currentLocation, fenceRadius)]
        } else {
            def fence = (fenceConfig.polygon as List).collect{ List<Double> point -> new Point(point[0], point[1]) }
            event['response.geo.fencing.distance'] = [distance: metersOutsidePolygon(currentLocation, fence).orElse(0)]
        }

        event
    }

}