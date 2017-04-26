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
package net.kpipes.endpoint.http.spring

import net.kpipes.core.KPipes
import net.kpipes.endpoint.http.AnonymousAuthenticator
import net.kpipes.endpoint.http.Authenticator as KPipesAuthenticator
import net.kpipes.endpoint.http.HttpEndpoint
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpEndpointConfig {

    @Bean(initMethod = 'start')
    HttpEndpoint httpEndpoint(KPipes kPipes, BrokerAdmin brokerAdmin, KPipesAuthenticator authenticator,
                                   @Value('${http.port:8080}') int httpPort,
                                   @Value('${kafka.port:9092}') int kafkaPort,
                              KafkaProducer kafkaProducer) {
        new HttpEndpoint(kPipes, new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin), kafkaProducer, brokerAdmin, authenticator, httpPort, kafkaPort)
    }

    @Bean
    @ConditionalOnMissingBean
    KPipesAuthenticator authenticator() {
        new AnonymousAuthenticator()
    }

}