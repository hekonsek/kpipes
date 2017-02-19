package net.kpipes.core

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterFunctionConfig {

    @Bean(name = 'filter')
    FilterFunction filterFunction() {
        new FilterFunction()
    }

}
