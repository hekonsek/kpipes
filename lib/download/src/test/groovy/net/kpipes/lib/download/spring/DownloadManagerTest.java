package net.kpipes.lib.download.spring;

import net.kpipes.lib.process.spring.ProcessManagerConfiguration;
import net.kpipes.lib.download.DownloadManager;
import net.kpipes.lib.download.DownloadManager.BinaryCoordinates;
import net.kpipes.lib.download.FileDownloadException;
import net.kpipes.lib.download.UnsupportedCompressionFormatException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.io.Files.createTempDir;
import static net.kpipes.lib.commons.Uuids.uuid;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DownloadManagerConfiguration.class, ProcessManagerConfiguration.class})
public class DownloadManagerTest {

    @Autowired
    DownloadManager downloadManager;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("download.directory", createTempDir().getAbsolutePath());
    }

    // Tests

    @Test
    public void shouldDownloadFile() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(
                new URL("http://search.maven.org/remotecontent?filepath=org/wildfly/swarm/guava/1.0.0.Alpha8/guava-1.0.0.Alpha8.jar"),
                "guava.jar"));

        // Then
        long guavaSize = downloadManager.downloadedFile("guava.jar").length();
        assertThat(guavaSize).isGreaterThan(0L);
    }

    @Test
    public void shouldDecompressTarGz() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(
                new File("src/test/compressedDirectory.tar.gz").toURI().toURL(),
                "compressedDirectory.tar.gz", "compressedDirectory"));

        // Then
        long uncompressedDirectory = downloadManager.downloadedFile("compressedDirectory").list().length;
        assertThat(uncompressedDirectory).isGreaterThan(0L);
    }

    @Test(expected = UnsupportedCompressionFormatException.class)
    public void shouldHandleUnsupportedCompressionFormat() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(new File("src/test/invalidCompressionFormat.xyz").toURI().toURL(),
                "invalidCompressionFormat.xyz", "invalidCompressionFormat"));
    }

    @Test(expected = FileDownloadException.class)
    public void shouldHandleInvalidHost() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(new URL("http://kpipes-" + uuid() + ".com"), uuid()));
    }

}
