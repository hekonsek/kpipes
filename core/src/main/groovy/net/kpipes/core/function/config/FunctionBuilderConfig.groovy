package net.kpipes.core.function.config

import net.kpipes.core.function.EventMappingFunctionBuilder
import net.kpipes.core.function.TableEventAggregateFunctionBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FunctionBuilderConfig {

    @Bean
    eventMappingFunctionBuilder() {
        new EventMappingFunctionBuilder()
    }

    @Bean
    tableEventAggregateFunctionBuilder() {
        new TableEventAggregateFunctionBuilder()
    }

}
