package net.kpipes.core.event

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class EventSerializerTest {

    def serializer = new EventSerializer()

    @Test
    void shouldDeserializeBody() {
        // Given
        def serializedEvent = serializer.serialize(new Event([:], [:], 'body'))

        // When
        def deserializedEvent = serializer.deserialize(serializedEvent)

        // Then
        assertThat(deserializedEvent.body()).isEqualTo('body')
    }

}
