package net.kpipes.core.store

import net.kpipes.core.KPipesConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StoreConfig {

    @Bean
    FileSystemKeyValueStore fileSystemKeyValueStore(KPipesConfig config) {
        def home = config.applicationHome()
        new FileSystemKeyValueStore(new File(home, '/store/fileSystemKeyValue'))
    }

}