package de.xstoff.homeserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ServerLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadServerFromFile() throws Exception {
        Path filePath = tempDir.resolve("servers.txt");
        String content = "Jellyfin;192.168.1.100;8096";
        Files.writeString(filePath, content, StandardCharsets.UTF_8);

        ServerLoader serverLoader = new ServerLoader();
        var servers = serverLoader.load(filePath);
        assertEquals(1, servers.size());

        Server server = servers.get(0);

        assertEquals("Jellyfin", server.name());
        assertEquals("192.168.1.100", server.ipAddress());
        assertEquals(8096, server.port());
    }
    @Test
    void shouldRejectLineWithoutPort() throws Exception {
        Path filePath = tempDir.resolve("servers.txt");
        String content = "Jellyfin;192.168.1.100";
        Files.writeString(filePath, content, StandardCharsets.UTF_8);

        ServerLoader serverLoader = new ServerLoader();
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> serverLoader.load(filePath)
        );
        assertEquals(
            "Invalid server configuration: Jellyfin;192.168.1.100",
            exception.getMessage()
        );
    }
    @Test
    void shouldRejectNonNumericPort() throws Exception {
        Path filePath = tempDir.resolve("servers.txt");
        String content = "Jellyfin;192.168.1.100;abc";
        Files.writeString(filePath, content, StandardCharsets.UTF_8);

        ServerLoader serverLoader = new ServerLoader();
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> serverLoader.load(filePath)
        );
        assertEquals(
            "Invalid port: abc",
            exception.getMessage()
        );
    }
}