package net.kpipes.core

import org.junit.Test

import static java.util.Optional.empty
import static net.kpipes.core.PipeDefinitionEncoder.encodePipe
import static org.assertj.core.api.Assertions.assertThat

class PipeDefinitionEncoderTest {

    @Test
    void shouldEncodeTarget() {
        // Given
        def pipe = new PipeDefinition('tenant', 'from', 'function', [:], Optional.of('target'))

        // When
        def encodedPipe = encodePipe(pipe)

        // Then
        assertThat(encodedPipe).endsWith('| target')
    }

    @Test
    void shouldEncodeNoTarget() {
        // Given
        def pipe = new PipeDefinition('tenant', 'from', 'function', [foo: 'bar'], empty())

        // When
        def encodedPipe = encodePipe(pipe)

        // Then
        assertThat(encodedPipe).endsWith(']')
    }

}