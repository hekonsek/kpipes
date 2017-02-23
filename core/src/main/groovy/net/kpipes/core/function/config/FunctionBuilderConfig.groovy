package net.kpipes.core.function.config

import net.kpipes.core.function.EventMappingFunctionBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FunctionBuilderConfig {

    @Bean
    eventMappingFunctionBuilder() {
        new EventMappingFunctionBuilder()
    }

}
