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
package net.kpipes.core.starter

import net.kpipes.core.starter.spi.Service
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class KPipesTest {

    static kpipes = new KPipes().start()

    @Service(overridable = true)
    def Integer small(KPipes kpipes) {
        10
    }

    @Service
    def Integer big(KPipes kpipes) {
        100
    }

    @Service(overridable = true)
    def Long overridable(KPipes kpipes) {
        10
    }

    // Tests

    @Test
    void shouldRetrieveService() {
        // Given
        kpipes.serviceRegistry().registerService('foo')

        // When
        def stringService = kpipes.service(String)

        // Then
        assertThat(stringService).contains('foo')
    }

    @Test
    void shouldNotRetrieveService() {
        // When
        def service = kpipes.service(Double)

        // Then
        assertThat(service).isEmpty()
    }

    @Test
    void shouldRetrieveServiceFromFactory() {
        // When
        def stringService = kpipes.service(Date)

        // Then
        assertThat(stringService).isNotNull()
    }

    @Test
    void shouldExecuteStartCallback() {
        // When
        def service = kpipes.service(ServiceClass).get()

        // Then
        assertThat(service.started).isTrue()
    }

    @Test
    void shouldOverrideServiceDefinition() {
        // When
        def service = kpipes.service(Integer)

        // Then
        assertThat(service).contains(100)
    }

    @Test
    void shouldNotOverrideServiceDefinition() {
        // When
        def service = kpipes.service(Long)

        // Then
        assertThat(service).contains(10L)
    }

    // Fixture classes

    static class ServiceFactories {

        @Service
        Date date(KPipes kpipes) {
            assertThat(kpipes).isNotNull()
            new Date()
        }

        @Service(onStart = 'start')
        def serviceClass(KPipes kpipes) {
            new ServiceClass()
        }

    }

    static class ServiceClass {

        boolean started

        void start() {
            started = true
        }

    }

}
