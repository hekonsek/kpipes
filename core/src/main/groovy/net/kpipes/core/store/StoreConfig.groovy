package net.kpipes.core.store

import net.kpipes.core.KPipesContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StoreConfig {

    @Bean
    FileSystemKeyValueStore fileSystemKeyValueStore(KPipesContext kPipesContext) {
        def home = kPipesContext.home()
        new FileSystemKeyValueStore(new File(home, '/store/fileSystemKeyValue'))
    }

}