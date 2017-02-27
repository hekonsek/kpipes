package net.kpipes.functions.geo.googlemaps

import net.kpipes.core.function.Event
import org.apache.commons.io.IOUtils
import org.junit.Test

import static java.io.File.createTempFile
import static java.nio.file.Files.probeContentType
import static org.assertj.core.api.Assertions.assertThat

class GeoGoogleMapsRenderEventMappingFunctionTest {

    @Test
    void shouldGenerateMapUrl() {
        // Given
        def function = new GeoGoogleMapsRenderEventFunction()
        def fenceConfig = [fence: [center: [lat: 49.820813, lng: 19.054982], radius: 80]]

        // When
        def result = function.onEvent(new Event('key', [lat: 49.820829, lng: 19.056378], fenceConfig, null))

        // Then
        def mapUrl = result['response.geo.googlemaps.render'].mapUrl as URL

        // Then
        def mapFile = createTempFile('kpipes', 'tmp')
        IOUtils.copy(mapUrl.openStream(), new FileOutputStream(mapFile))
        def mapContentType = probeContentType(mapFile.toPath())
        assertThat(mapContentType).isEqualTo('image/png')
    }

}
