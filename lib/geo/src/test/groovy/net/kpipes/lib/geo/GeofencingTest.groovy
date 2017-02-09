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

import org.assertj.core.api.Assertions
import org.junit.Test

import static Geofencing.*
import static Point.point

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
        Assertions.assertThat(isWithin).isTrue()
    }

    @Test
    void shouldNotDetectPointWIthinPolygon() {
        def point = new Point(100,100)
        def polygon = [new Point(0,0), new Point(1,2), new Point(2,1)]

        // When
        def isWithin = isPointWithinPolygon(point, polygon)

        // Then
        Assertions.assertThat(isWithin).isFalse()
    }

    @Test
    void shouldCalculateMetersBetweenPoints() {
        // When
        def distanceInKilometers = metersBetweenPoints(bielskoCity, gdyniaCity) / 1000

        // Then
        Assertions.assertThat(distanceInKilometers).isGreaterThan(500d)
        Assertions.assertThat(distanceInKilometers).isLessThan(550d)
    }

    @Test
    void distanceBetweenPointsShouldBeReflective() {
        // When
        def bielskoGdyniaDistance = metersBetweenPoints(bielskoCity, gdyniaCity)
        def gdyniaBielskoDistance = metersBetweenPoints(gdyniaCity, bielskoCity)

        // Then
        Assertions.assertThat(bielskoGdyniaDistance).isEqualTo(gdyniaBielskoDistance)
    }

    @Test
    void isShouldNotBeInAreaOfGdynia() {
        // Given
        def fenceRadius = 500 * 1000
        def fiftyKilometers = 50 * 1000

        // When
        def outsizeOfFence = metersOutsideFence(gdyniaCity, bielskoCity, fenceRadius)

        // Then
        Assertions.assertThat(outsizeOfFence).isBetween(0d, fiftyKilometers.doubleValue())
    }


}
