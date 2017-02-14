package net.kpipes.functions.geo.geofencing

import net.kpipes.core.KPipes
import net.kpipes.core.starter.spi.Service

import static net.kpipes.core.function.FunctionBinding.functionBinding

class GeoFencingConfig {

    @Service(onStart = 'start')
    def geoFencingDistanceFunctionBinding(KPipes kpipes) {
        functionBinding(kpipes, 'geo.fencing.distance', new GeoFencingDistanceFunction())
    }

}
