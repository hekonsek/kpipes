package net.kpipes.lib.kafka.client

import org.assertj.core.api.Assertions
import org.junit.Test

import static net.kpipes.lib.commons.Uuids.uuid

class KafkaConsumerBuilderTest {

    @Test
    void shouldCreateConsumer() {
        def consumer = new KafkaConsumerBuilder<>(uuid()).build()
        Assertions.assertThat(consumer).isNotNull()
    }

}
