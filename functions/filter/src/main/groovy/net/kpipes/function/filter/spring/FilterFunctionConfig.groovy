package net.kpipes.function.filter.spring

import net.kpipes.function.filter.FilterFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterFunctionConfig {

    @Bean(name = 'filter')
    filterFunction() {
        new FilterFunction()
    }

}
