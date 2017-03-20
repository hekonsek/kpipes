package net.kpipes.function.view.materialize.keyvalue.spring

import net.kpipes.function.view.materialize.keyvalue.MaterializeKeyValueViewFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MaterializeKeyValueViewFunctionConfig {

    @Bean(name = 'view.materialize.keyvalue')
    MaterializeKeyValueViewFunction materializeKeyValueViewFunction() {
        new MaterializeKeyValueViewFunction()
    }

}
