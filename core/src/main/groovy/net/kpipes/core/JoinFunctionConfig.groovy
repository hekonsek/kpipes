package net.kpipes.core

import net.kpipes.lib.kafka.client.BrokerAdmin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JoinFunctionConfig {

    @Bean(name = 'join')
    joinFunction(BrokerAdmin brokerAdmin) {
        new JoinFunction(brokerAdmin)
    }

}
