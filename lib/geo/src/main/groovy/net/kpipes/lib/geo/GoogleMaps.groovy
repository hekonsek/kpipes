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

import com.google.common.collect.Lists
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.LatLng

import static java.lang.Math.*

class GoogleMaps {

    private static final String MAP_API_BASE = "https://maps.googleapis.com/maps/api/staticmap";

    public static String encodeRoute(List<Point> coordinates) {
        return PolylineEncoding.encode(coordinates.collect{ new LatLng(it.lat(), it.lng()) })
    }

    public static String encodeRoute(Point... coordinates) {
        return encodeRoute(Arrays.asList(coordinates))
    }

    public static String extractEncodedPath(URL url) {
        String urlString = url.toString();
        return urlString.substring(urlString.indexOf("enc:") + 4);
    }

    static URL renderRouteUrl(Point... coordinates) {
        return renderRouteUrl(Lists.newArrayList(coordinates));
    }

    static URL renderRouteUrl(List<Point> coordinates) {
        try {
            return new URL(MAP_API_BASE + "?size=400x400&path=weight:5%7Ccolor:0x0000ff%7Cenc:" + encodeRoute(coordinates));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeMarker(Point coordinates, char marketText, String color) {
        return "color:" + color + "%7Clabel:" + marketText + "%7C" + coordinates.lat + "," + coordinates.lng;
    }

    public static URL renderCircleFenceMapUrl(Point center, double fenceRadiusInMeters, Point marker) {
        String circle = encodeCircle(center, fenceRadiusInMeters);
        String encodedMarker = encodeMarker(marker, 'D' as char, "blue");

        try {
            return new URL("http://maps.google.com/maps/api/staticmap?center=" + center.lat + "," + center.lng + "&size=640x480&maptype=roadmap&path=fillcolor:0xE85F0E33%7Ccolor:0x'91A93A'00%7Cenc:" + circle + "&sensor=false&markers=" + encodedMarker);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeCircle(Point center, double radiusInMeters) {
        double radiusInKilometers = radiusInMeters / 1000

        double lat = (center.lat() * PI) / 180;
        double lng = (center.lng() * PI) / 180;

        int precision = 8

        double d = radiusInKilometers / Geofencing.EARTH_RADIUS_IN_KILOMETERS

        def points = new LinkedList<>();
        for (int i = 0; i <= 360; i += precision) {
            double brng = i * PI / 180;

            double pLat = asin(sin(lat) * cos(d) + cos(lat) * sin(d) * cos(brng))
            double pLng = ((lng + atan2(sin(brng) * sin(d) * cos(lat), cos(d) - sin(lat) * sin(pLat))) * 180) / PI
            pLat = (pLat * 180) / PI

            points.add(new LatLng(pLat, pLng));
        }

        return PolylineEncoding.encode(points);
    }

}
