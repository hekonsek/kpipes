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
package net.kpipes.core.function.config

import net.kpipes.core.KPipesContext
import net.kpipes.core.function.EventMappingFunctionBuilder
import net.kpipes.core.function.EventAggregateFunctionBuilder
import net.kpipes.core.function.EventRoutingFunctionBuilder
import net.kpipes.core.function.EventStreamFunctionBuilder

import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FunctionBuilderConfig {

    @Bean
    eventMappingFunctionBuilder(KPipesContext kPipesContext) {
        new EventMappingFunctionBuilder(kPipesContext)
    }

    @Bean
    eventAggregateFunctionBuilder(KPipesContext kpipesContext) {
        new EventAggregateFunctionBuilder(kpipesContext)
    }

    @Bean
    eventTableFunctionBuilder() {
        new EventStreamFunctionBuilder()
    }

    @Bean
    eventRoutingFunctionBuilder(KPipesContext kpipesContext, KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin) {
        new EventRoutingFunctionBuilder(kpipesContext, kafkaProducer, brokerAdmin)
    }

}
