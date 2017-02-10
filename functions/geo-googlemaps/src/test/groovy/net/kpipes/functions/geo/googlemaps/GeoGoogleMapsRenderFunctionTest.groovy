package net.kpipes.functions.geo.googlemaps

import net.kpipes.core.event.Event
import org.apache.commons.io.IOUtils
import org.junit.Test

import java.nio.file.Files

import static java.io.File.createTempFile
import static org.assertj.core.api.Assertions.assertThat

class GeoGoogleMapsRenderFunctionTest {

    @Test
    void shouldGenerateMapUrl() {
        // Given
        def function = new GeoGoogleMapsRenderFunction()
        def fenceConfig = [fence: [center: [lat: 49.820813, lng: 19.054982], radius: 80]]

        // When
        def result = function.apply(new Event([config: fenceConfig], [:], [lat: 49.820829, lng: 19.056378]))

        // Then
        def mapUrl = result.metaData()['response.geo.googlemaps.render'].mapUrl as URL

        // Then
        def mapFile = createTempFile('kpipes', 'tmp')
        IOUtils.copy(mapUrl.openStream(), new FileOutputStream(mapFile))
        def mapContentType = Files.probeContentType(mapFile.toPath())
        assertThat(mapContentType).isEqualTo('image/png')
    }

}
