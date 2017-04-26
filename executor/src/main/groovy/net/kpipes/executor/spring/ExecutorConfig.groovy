package net.kpipes.executor.spring

import net.kpipes.core.Json
import net.kpipes.core.KPipes
import net.kpipes.executor.FunctionExecutor
import net.kpipes.executor.ServiceEventPipeline
import net.kpipes.executor.ServiceRegistryFunctionExecutor
import net.kpipes.lib.commons.KPipesConfig
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ExecutorConfig {

    @Bean(initMethod = 'start')
    ServiceEventPipeline serviceEventPipeline(KPipesConfig config,
                                              Json json,
                                              KafkaConsumerTemplate consumer,
                                              KafkaProducer producer,
                                              List<FunctionExecutor> functionExecutors) {
        new ServiceEventPipeline(config, json, consumer, producer, functionExecutors)
    }

    @Bean
    FunctionExecutor serviceRegistryFunctionExecutor(KPipes kPipes) {
        new ServiceRegistryFunctionExecutor(kPipes)
    }

}
