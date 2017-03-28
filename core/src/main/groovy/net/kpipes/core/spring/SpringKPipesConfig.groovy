package net.kpipes.core.spring

import net.kpipes.core.EventEncoder
import net.kpipes.core.Json
import net.kpipes.core.JsonEventEncoder
import net.kpipes.core.KPipes
import net.kpipes.core.KPipesConfig
import net.kpipes.core.PipeBuilder
import net.kpipes.core.ServiceRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication(scanBasePackages = 'net.kpipes')
class SpringKPipesConfig {

    @Bean
    KPipes kpipesContext(ServiceRegistry serviceRegistry, PipeBuilder pipeBuilder) {
        new KPipes(serviceRegistry, pipeBuilder)
    }

    @Bean
    ServiceRegistry serviceRegistry(ApplicationContext applicationContext) {
        new SpringServiceRegistry(applicationContext)
    }

    @Bean
    PipeBuilder pipeBuilder(KPipesConfig config, ServiceRegistry serviceRegistry) {
        new PipeBuilder(config, serviceRegistry)
    }

    @Bean
    KPipesConfig kpipesConfig(@Value('${applicationId}') String applicationId,
                              @Value('${nodeId}') String nodeId,
                              @Value('${kafka.host:localhost}') String kafkaHost,
                              @Value('${kafka.port:9092}') int kafkaPort,
                              @Value('${zooKeeper.host:localhost}') String zooKeeperHost,
                              @Value('${zooKeeper.port:2181}') int zooKeeperPort,
                              @Value('${kpipes.home:/var/kpipes}') File kpipesHome) {
        new KPipesConfig(applicationId, nodeId, kafkaHost, kafkaPort, zooKeeperHost, zooKeeperPort, kpipesHome)
    }

    @Bean
    Json json() {
        new Json()
    }

    @ConditionalOnMissingBean
    @Bean
    EventEncoder eventEncoder() {
        new JsonEventEncoder()
    }

}
