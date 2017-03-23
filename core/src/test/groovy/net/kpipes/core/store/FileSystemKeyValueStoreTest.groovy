package net.kpipes.core.store

import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

class FileSystemKeyValueStoreTest {

    def home = createTempDir()

    def store = new FileSystemKeyValueStore(home)

    @Test
    void shouldReturnEmptyCollection() {
        assertThat(store.all(uuid())).isEmpty()
    }

    @Test
    void shouldCountZeroEmptyCollection() {
        assertThat(store.count(uuid())).isEqualTo(0)
    }

}
