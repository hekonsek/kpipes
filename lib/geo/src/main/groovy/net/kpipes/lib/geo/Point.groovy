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

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Point implements Serializable {

    // Members

    private final double lat

    private final double lng

    // Constructors

    Point(double lat, double lng) {
        this.lat = lat
        this.lng = lng
    }

    // Factory methods

    static Point point(double lat, double lng) {
        new Point(lat, lng)
    }

    // Getters & setters

    double lat() {
        lat
    }

    double lng() {
        lng
    }

}
