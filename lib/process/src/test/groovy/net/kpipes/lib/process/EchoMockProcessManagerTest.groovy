package net.kpipes.lib.process

import net.kpipes.lib.process.EchoMockProcessManager
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static net.kpipes.lib.process.Command.cmd

class EchoMockProcessManagerTest {

    def processManager = new EchoMockProcessManager()

    // Tests

    @Test
    void shouldReturnEcho() {
        def output = processManager.execute(cmd('foo'))
        assertThat(output).isEqualTo(['foo'])
    }

    @Test
    void shouldReturnEchoAsynchronously() {
        def output = processManager.executeAsync(cmd('foo')).get()
        assertThat(output).isEqualTo(['foo'])
    }

}
