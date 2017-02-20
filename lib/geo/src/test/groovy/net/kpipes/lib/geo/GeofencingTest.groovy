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

import org.junit.Test

import static Point.point
import static net.kpipes.lib.geo.Geofencing.*
import static org.assertj.core.api.Assertions.assertThat

class GeofencingTest {

    def bielskoCity = point(49.8287954,19.0299223)

    def gdyniaCity = point(54.4843721, 18.4982603)

    @Test
    void shouldDetectPointWIthinPolygon() {
        def point = new Point(1,1)
        def polygon = [new Point(0,0), new Point(1,2), new Point(2,1)]

        // When
        def isWithin = isPointWithinPolygon(point, polygon)

        // Then
        assertThat(isWithin).isTrue()
    }

    @Test
    void shouldNotDetectPointWIthinPolygon() {
        def point = new Point(100,100)
        def polygon = [new Point(0,0), new Point(1,2), new Point(2,1)]

        // When
        def isWithin = isPointWithinPolygon(point, polygon)

        // Then
        assertThat(isWithin).isFalse()
    }

    @Test
    void pointsFromPolygonShouldNotReturnDistanceFromPolygon() {
        def point = new Point(1,1)
        def polygon = [new Point(0,0), new Point(1,2), new Point(2,1)]

        // When
        def isWithin = metersOutsidePolygon(point, polygon)

        // Then
        assertThat(isWithin).isEmpty()
    }

    @Test
    void shouldReturnDistanceFromPolygon() {
        def point = new Point(49.8209418, 19.0551721)
        def polygon = [new Point(49.821152, 19.054440), new Point(49.820680, 19.054596), new Point(49.820644, 19.055739)]

        // When
        def distance = metersOutsidePolygon(point, polygon)

        // Then
        assertThat(distance.get()).isBetween(50.0d, 51.0d)
    }

    @Test
    void shouldCalculateMetersBetweenPoints() {
        // When
        def distanceInKilometers = metersBetweenPoints(bielskoCity, gdyniaCity) / 1000

        // Then
        assertThat(distanceInKilometers).isGreaterThan(500d)
        assertThat(distanceInKilometers).isLessThan(550d)
    }

    @Test
    void distanceBetweenPointsShouldBeReflective() {
        // When
        def bielskoGdyniaDistance = metersBetweenPoints(bielskoCity, gdyniaCity)
        def gdyniaBielskoDistance = metersBetweenPoints(gdyniaCity, bielskoCity)

        // Then
        assertThat(bielskoGdyniaDistance).isEqualTo(gdyniaBielskoDistance)
    }

    @Test
    void isShouldNotBeInAreaOfGdynia() {
        // Given
        def fenceRadius = 500 * 1000
        def fiftyKilometers = 50 * 1000

        // When
        def outsizeOfFence = metersOutsideCircle(gdyniaCity, bielskoCity, fenceRadius)

        // Then
        assertThat(outsizeOfFence).isBetween(0d, fiftyKilometers.doubleValue())
    }


}
