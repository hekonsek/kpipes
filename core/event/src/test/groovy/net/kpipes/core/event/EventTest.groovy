package net.kpipes.core.event

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class EventTest {

    @Test
    void shouldReturnEntityId() {
        def entityId = new Event([entityId: 'foo'], [:], [foo: 'bar']).entityId()
        assertThat(entityId).contains('foo')
    }

    @Test
    void shouldNotReturnEntityId() {
        def entityId = new Event([:]).entityId()
        assertThat(entityId).isEmpty()
    }

    @Test
    void shouldReturnTenant() {
        def tenant = new Event([tenant: 'foo'], [:], [:]).tenant()
        assertThat(tenant).contains('foo')
    }

    @Test
    void shouldNotReturnTenant() {
        def tenant = new Event([:]).tenant()
        assertThat(tenant).isEmpty()
    }

}
