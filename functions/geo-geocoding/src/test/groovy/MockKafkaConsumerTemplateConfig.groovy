import net.kpipes.core.starter.KPipes
import net.kpipes.core.starter.spi.Service
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate

import static org.mockito.Mockito.mock

class MockKafkaConsumerTemplateConfig {

    @Service
    KafkaConsumerTemplate kafkaConsumerTemplate(KPipes kPipes) {
        mock(KafkaConsumerTemplate)
    }

}
