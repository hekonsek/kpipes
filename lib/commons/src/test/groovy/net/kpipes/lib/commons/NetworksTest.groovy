package net.kpipes.lib.commons

import org.assertj.core.api.Assertions
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class NetworksTest {

    @Test
    void should() {
        // Given
        def first = Networks.availableTcpPort()

        // When
        def second = Networks.availableTcpPort()

        // Then
        assertThat(first).isLessThan(second)
    }

}
