package net.kpipes.core

import org.junit.Test

import static java.util.Optional.empty
import static net.kpipes.core.PipeDefinitionEncoder.decodePipe
import static net.kpipes.core.PipeDefinitionEncoder.encodePipe
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

class PipeDefinitionEncoderTest {

    def tenant = uuid()

    // Tests

    @Test
    void shouldEncodeTarget() {
        // Given
        def pipe = new PipeDefinition(tenant, empty(), 'from', 'function', [:], Optional.of('target'))

        // When
        def encodedPipe = encodePipe(pipe)

        // Then
        assertThat(encodedPipe).endsWith('| target')
    }

    @Test
    void shouldEncodeNoTarget() {
        // Given
        def pipe = new PipeDefinition(tenant, empty(), 'from', 'function', [foo: 'bar'], empty())

        // When
        def encodedPipe = encodePipe(pipe)

        // Then
        assertThat(encodedPipe).endsWith(']')
    }

    @Test
    void shouldDecodeStringInConfig() {
        // Given
        def pipe = 'foo | function [expression: "hello world"] | bar'

        // When
        def decodedPipe = decodePipe(tenant, pipe)

        // Then
        assertThat(decodedPipe.functionConfiguration()).containsEntry('expression', 'hello world')
    }

}