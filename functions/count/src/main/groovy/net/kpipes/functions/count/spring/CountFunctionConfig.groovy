package net.kpipes.functions.count.spring

import net.kpipes.functions.count.CountFunctionTable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CountFunctionConfig {

    @Bean(name = 'count')
    countFunction() {
        new CountFunctionTable()
    }

}
