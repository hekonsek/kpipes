package net.kpipes.core.event

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class EventTest {

    @Test
    void shouldReturnEntityId() {
        def entityId = new Event([entityId: 'foo'], [:], 'body').entityId()
        assertThat(entityId).contains('foo')
    }

}
