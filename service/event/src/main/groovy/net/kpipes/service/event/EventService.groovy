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
package net.kpipes.service.event

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.adapter.Tenant
import net.kpipes.core.store.FileSystemKeyValueStore
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.commons.lang3.Validate
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.slf4j.Logger

import static net.kpipes.lib.commons.Uuids.uuid
import static org.slf4j.LoggerFactory.getLogger

class EventService {

    private final static Logger LOG = getLogger(EventService)

    private final KafkaProducer kafkaProducer

    private final BrokerAdmin brokerAdmin

    private final FileSystemKeyValueStore store

    EventService(KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin, FileSystemKeyValueStore store) {
        this.kafkaProducer = kafkaProducer
        this.brokerAdmin = brokerAdmin
        this.store = store
    }

    // Operations

    List<String> list(@Tenant String tenant) {
        Validate.notBlank(tenant, 'Tenant cannot be blank.')

        LOG.debug('Listing event topics for tenant {}', tenant)
        brokerAdmin.eventTopics().collect{ it.replaceFirst(/$tenant\./, '') }
    }

    void add(@Tenant String tenant, String topic, String key, Map<String, Object> event) {
        Validate.notBlank(tenant, 'Tenant cannot be blank.')
        Validate.notBlank(topic, 'Topic cannot be blank.')
        if(key == null) {
            key = uuid()
            LOG.debug('No key specified, generated key: {}', key)
        }

        def effectiveTopic = "${tenant}.${topic}"
        brokerAdmin.ensureTopicExists(effectiveTopic)
        kafkaProducer.send(new ProducerRecord(effectiveTopic, key, new Bytes(new ObjectMapper().writeValueAsBytes(event))))
    }

    Map<String, Map<String, Object>> view(@Tenant String tenant, String topic) {
        Validate.notBlank(tenant, 'Tenant cannot be blank.')
        Validate.notBlank(topic, 'Topic cannot be blank.')

        def collection = "${tenant}.${topic}"
        if(!store.exists(collection)) {
            throw new IllegalStateException("Materialized view of topic ${topic} for tenant ${tenant} doesn't exist. Have you materialized it?")
        }
        def results = [:]
        store.all(collection).each {
            results[it.key] = new ObjectMapper().readValue(it.value, Map)
        }
        results
    }

    long count(@Tenant String tenant, String topic) {
        Validate.notBlank(tenant, 'Tenant cannot be blank.')
        Validate.notBlank(topic, 'Topic cannot be blank.')

        def collection = "${tenant}.${topic}"
        if(!store.exists(collection)) {
            throw new IllegalStateException("Materialized view of topic ${topic} for tenant ${tenant} doesn't exist. Have you materialized it?")
        }
        store.count(collection)
    }

}
