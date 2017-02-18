package net.kpipes.functions.notification.spring

import net.kpipes.functions.notification.NotificationFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NotificationFunctionConfig {

    @Bean(name = 'notification')
    def notificationFunction() {
        new NotificationFunction()
    }

}