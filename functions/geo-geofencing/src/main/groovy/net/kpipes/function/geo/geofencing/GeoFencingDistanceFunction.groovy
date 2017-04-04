package net.kpipes.function.geo.geofencing

import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction
import net.kpipes.lib.geo.Point

import static net.kpipes.lib.geo.Geofencing.metersOutsideCircle
import static net.kpipes.lib.geo.Geofencing.metersOutsidePolygon
import static org.slf4j.LoggerFactory.getLogger

class GeoFencingDistanceFunction implements EventMappingFunction {

    private static final LOG = getLogger(GeoFencingDistanceFunction)

    @Override
    Map<String, Object> onEvent(Event event) {
        def currentLocation = new Point(event.body().lat as double, event.body().lng as double)
        LOG.debug('About effectiveTo invoke function against location: {}', currentLocation)
        def fenceConfig = event.config().fence as Map

        if(fenceConfig.containsKey('center')) {
            def fenceCenter = new Point(fenceConfig.center.lat as double, fenceConfig.center.lng as double)
            def fenceRadius = fenceConfig.radius as double
            event.body()['response.geo.fencing.distance'] = [distance: metersOutsideCircle(fenceCenter, currentLocation, fenceRadius)]
        } else {
            def fence = (fenceConfig.polygon as List).collect{ List<Double> point -> new Point(point[0], point[1]) }
            event.body()['response.geo.fencing.distance'] = [distance: metersOutsidePolygon(currentLocation, fence).orElse(0)]
        }

        event.body()
    }

}