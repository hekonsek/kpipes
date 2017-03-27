package net.kpipes.lib.process

import org.assertj.core.api.Assertions
import org.junit.Test

import static net.kpipes.lib.process.SudoResolver.resolveSudo

class SudoResolverTest {

    @Test
    void nonRootWithNonEmptyPasswordShouldUseSudoInPipe() {
        // Given
        System.setProperty('user.name', 'notRoot')
        def command = CommandBuilder.sudo('echo foo').sudoPassword('nonEmptyPassword').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand.last()).contains('sudo')
    }

    @Test
    void nonRootWithBlankPasswordShouldUseSudoInPipe() {
        // Given
        System.setProperty('user.name', 'notRoot')
        def command = CommandBuilder.sudo('echo foo').sudoPassword(' ').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand.last()).contains('sudo')
    }

    @Test
    void nonRootWithEmptyPasswordShouldUseSudoPrefix() {
        // Given
        System.setProperty('user.name', 'notRoot')
        def command = CommandBuilder.sudo('echo foo').sudoPassword('').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand.first()).isEqualTo('sudo')
    }

    @Test
    void rootShouldNotUseSudo() {
        // Given
        System.setProperty('user.name', 'root')
        def command = CommandBuilder.sudo('echo foo').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand).isEqualTo(['echo', 'foo'])
    }

}
