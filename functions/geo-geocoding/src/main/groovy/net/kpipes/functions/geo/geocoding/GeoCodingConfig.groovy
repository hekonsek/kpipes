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
package net.kpipes.functions.geo.geocoding

import net.kpipes.core.starter.KPipes
import net.kpipes.core.starter.spi.Service
import org.apache.camel.ProducerTemplate
import org.apache.camel.impl.DefaultCamelContext

import static net.kpipes.core.function.FunctionBinding.functionBinding

class GeoCodingConfig {

    @Service(onStart = 'start')
    def coreFilterFunctionBinding(KPipes kpipes) {
        def camelProducerTemplate = kpipes.service(ProducerTemplate)
        functionBinding(kpipes, 'geo.coding.reverse', new GeoCodingReverseFunction(camelProducerTemplate))
    }

    @Service
    def camelProducerTemplate(KPipes kpipes) {
        def camel = new DefaultCamelContext()
        camel.start()
        camel.createProducerTemplate()
    }

}