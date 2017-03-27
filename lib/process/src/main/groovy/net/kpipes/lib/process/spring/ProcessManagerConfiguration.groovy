package net.kpipes.lib.process.spring

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import net.kpipes.lib.process.DefaultProcessManager
import net.kpipes.lib.process.ProcessManager

@Configuration
class ProcessManagerConfiguration {

    @Bean(destroyMethod = 'close')
    @ConditionalOnMissingBean
    ProcessManager processManager() {
        new DefaultProcessManager()
    }

}
