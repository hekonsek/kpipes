package net.kpipes.core

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CountFunctionConfig {

    @Bean(name = 'count')
    countFunction() {
        new CountFunction()
    }

}
