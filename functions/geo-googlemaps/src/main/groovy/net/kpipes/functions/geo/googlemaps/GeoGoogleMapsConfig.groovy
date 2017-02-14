package net.kpipes.functions.geo.googlemaps

import net.kpipes.core.KPipes
import net.kpipes.core.starter.spi.Service

import static net.kpipes.core.function.FunctionBinding.functionBinding

class GeoGoogleMapsConfig {

    @Service(onStart = 'start')
    def geoGoogleMapsRenderFunctionBinding(KPipes kpipes) {
        functionBinding(kpipes, 'geo.googlemaps.render', new GeoGoogleMapsRenderFunction())
    }

}
