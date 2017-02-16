package net.kpipes.lib.kafka.client.spring

import net.kpipes.lib.kafka.client.BrokerAdmin
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BrokerAdminConfig {

    @Bean
    BrokerAdmin brokerAdmin(@Value('${zooKeeper.host:localhost}') String zooKeeperHost,
                            @Value('${zooKeeper.port:2181}') int zooKeeperPort) {
        new BrokerAdmin(zooKeeperHost, zooKeeperPort)
    }

}
