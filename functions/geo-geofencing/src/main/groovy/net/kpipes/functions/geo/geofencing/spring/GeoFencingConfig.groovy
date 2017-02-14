package net.kpipes.functions.geo.geofencing.spring

import net.kpipes.functions.geo.geofencing.GeoFencingDistanceFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeoFencingConfig {

    @Bean(name = 'geo.fencing.distance')
    def geoFencingDistanceFunction() {
        new GeoFencingDistanceFunction()
    }

}
