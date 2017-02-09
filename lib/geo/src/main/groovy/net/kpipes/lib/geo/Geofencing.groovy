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

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory

import static java.lang.Math.PI
import static java.lang.Math.sin

import static java.lang.Math.*

class Geofencing {

    public static final double EARTH_RADIUS_IN_KILOMETERS = 6371;

    static boolean isPointWithinPolygon(Point point, List<Point> polygon) {
        def polygonCoordinates = polygon.collect{ new Coordinate(it.lat(), it.lng()) }
        if(polygonCoordinates.first() != polygonCoordinates.last()) {
            polygonCoordinates << polygonCoordinates.first()
        }

        def geometryFactory = new GeometryFactory()
        def jtsPolygon = geometryFactory.createPolygon(polygonCoordinates.toArray(new Coordinate[0]))
        geometryFactory.createPoint(new Coordinate(point.lat(), point.lng())).within(jtsPolygon)
    }

    public static double gpsCoordinateToRadius(double value) {
        return (value * PI) / 180;
    }

    public static double metersBetweenPoints(Point a, Point b) {
        double dLat = gpsCoordinateToRadius(b.lat - a.lat);
        double dLon = gpsCoordinateToRadius(b.lng - a.lng);
        double lat1 = gpsCoordinateToRadius(a.lat);
        double lat2 = gpsCoordinateToRadius(b.lat);

        double aa = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * Math.cos(lat2);
        double c = 2 * atan2(sqrt(aa), sqrt(1 - aa));
        return EARTH_RADIUS_IN_KILOMETERS * c * 1000;
    }

    static double metersOutsideFence(Point center, Point marker, double fenceRadiusInMeters) {
        metersBetweenPoints(center, marker) - fenceRadiusInMeters
    }

}
