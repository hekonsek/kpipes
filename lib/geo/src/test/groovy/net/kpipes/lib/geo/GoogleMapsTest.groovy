/**
 * Licensed to the KPipes under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kpipes.lib.geo

import org.apache.commons.io.IOUtils
import org.junit.Test

import java.nio.file.Files

import static Point.point
import static java.io.File.createTempFile
import static org.assertj.core.api.Assertions.assertThat

class GoogleMapsTest {

    def mapFile = createTempFile('kpipes', 'tmp')

    @Test
    void shouldDownloadMapAsPng() {
        // Given
        def firstPoint = point(49.823873, 19.041077)
        def secondPoint = point(49.829472, 19.077234)

        // When
        def url = GoogleMaps.renderRouteUrl(firstPoint, secondPoint)
        IOUtils.copy(url.openStream(), new FileOutputStream(mapFile))

        // Then
        def mapContentType = Files.probeContentType(mapFile.toPath())
        assertThat(mapContentType).isEqualTo('image/png')
    }

    @Test
    void shouldDownloadMapWithCircleAsPng() {
        // Given
        def firstPoint = point(49.823873, 19.041077)
        def secondPoint = point(49.829472, 19.077234)

        // When
        def url = GoogleMaps.renderCircleFenceMapUrl(firstPoint, secondPoint, 500d)
        IOUtils.copy(url.openStream(), new FileOutputStream(mapFile))

        // Then
        def mapContentType = Files.probeContentType(mapFile.toPath())
        assertThat(mapContentType).isEqualTo('image/png')
    }

}
