package net.kpipes.core.store

import com.google.common.io.Files

class FileSystemKeyValueStore {

    private final File parentDirectory

    FileSystemKeyValueStore(File parentDirectory) {
        this.parentDirectory = parentDirectory
    }

    void save(String collection, String key, byte[] value) {
        def targetFile = new File(parentDirectory, "${collection}/${key}")
        targetFile.parentFile.mkdirs()
        Files.write(value, targetFile)
    }

    byte[] read(String collection, String key) {
        def targetFile = new File(parentDirectory, "${collection}/${key}")
        Files.toByteArray(targetFile)
    }

    long count(String collection) {
        new File(parentDirectory, collection).list().length
    }

    Map<String, byte[]> all(String collection) {
        def result = [:]
        new File(parentDirectory, collection).listFiles().each {
            result[it.name] = Files.toByteArray(it)
        }
        result
    }

}