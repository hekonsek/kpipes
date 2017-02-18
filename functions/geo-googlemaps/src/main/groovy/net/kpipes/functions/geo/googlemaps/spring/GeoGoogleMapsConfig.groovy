package net.kpipes.functions.geo.googlemaps.spring

import net.kpipes.functions.geo.googlemaps.GeoGoogleMapsRenderEventFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeoGoogleMapsConfig {

    @Bean(name = 'geo.googlemaps.render')
    def geoGoogleMapsRenderFunction() {
        new GeoGoogleMapsRenderEventFunction()
    }

}
