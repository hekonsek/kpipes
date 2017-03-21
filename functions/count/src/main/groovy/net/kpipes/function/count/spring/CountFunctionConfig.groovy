package net.kpipes.function.count.spring

import net.kpipes.function.count.CountFunctionTable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CountFunctionConfig {

    @Bean(name = 'count')
    countFunction() {
        new CountFunctionTable()
    }

}
