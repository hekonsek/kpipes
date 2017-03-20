package net.kpipes.cmd

import net.kpipes.core.KPipesApplication
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static net.kpipes.lib.commons.Mavens.kpipesVersion
import static org.assertj.core.api.Assertions.assertThat

class CmdTest {

    @Test
    void shouldGetKpipesVersion() {
        System.setProperty('zooKeeper.dataDirectory', createTempDir().absolutePath)
        System.setProperty('kafka.dataDirectory', createTempDir().absolutePath)

        KPipesApplication.main()
        def versionResponse = new Cmd().executeCommand('kpipes', 'version')
        assertThat(versionResponse).isEqualTo(kpipesVersion())
    }

}
