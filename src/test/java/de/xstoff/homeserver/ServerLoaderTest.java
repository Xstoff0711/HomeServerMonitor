package de.xstoff.homeserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ServerLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadServerFromFile() throws Exception {
        Path filePath = tempDir.resolve("servers.txt");
        String content = "Jellyfin;192.168.1.100;8096;HTTP";
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
        String content = "Jellyfin;192.168.1.100;HTTP";
        Files.writeString(filePath, content, StandardCharsets.UTF_8);

        ServerLoader serverLoader = new ServerLoader();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> serverLoader.load(filePath));
        assertEquals(
                "Invalid server configuration: Jellyfin;192.168.1.100;HTTP",
                exception.getMessage());
    }

    @Test
    void shouldRejectNonNumericPort() throws Exception {
        Path filePath = tempDir.resolve("servers.txt");
        String content = "Jellyfin;192.168.1.100;abc;TCP";
        Files.writeString(filePath, content, StandardCharsets.UTF_8);

        ServerLoader serverLoader = new ServerLoader();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> serverLoader.load(filePath));
        assertEquals(
                "Invalid port: abc",
                exception.getMessage());
    }

    @Test
    void shouldIgnoreBlankLines() throws Exception {
        Path filePath = tempDir.resolve("servers.txt");
        String content = """

                Jellyfin;192.168.1.100;8096;HTTP
                """;
        ;
        Files.writeString(filePath, content, StandardCharsets.UTF_8);
        ServerLoader serverLoader = new ServerLoader();
        var servers = serverLoader.load(filePath);
        assertEquals(1, servers.size());
        assertEquals("Jellyfin", servers.get(0).name());

    }

    @Test
    void shouldIgnoreComments() throws Exception {
        Path filePath = tempDir.resolve("servers.txt");

        String content = """
                # Meine Server
                Jellyfin;192.168.1.100;8096;HTTP
                """;

        Files.writeString(filePath, content, StandardCharsets.UTF_8);

        ServerLoader serverLoader = new ServerLoader();
        var servers = serverLoader.load(filePath);

        assertEquals(1, servers.size());
        assertEquals("Jellyfin", servers.get(0).name());
    }

    @Test 
    void shouldRejectLineWithInvalidCheckType() throws Exception {
        Path filePath = tempDir.resolve("servers.txt");
        String content = "Jellyfin;192.168.1.100;8096;FTP";
        Files.writeString(filePath, content, StandardCharsets.UTF_8);
        ServerLoader serverLoader = new ServerLoader();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> serverLoader.load(filePath));
        assertEquals(
                "Invalid check type: FTP",
                exception.getMessage());
    }

    @Test
    void shouldLoadHttpServerWithCustomPath() throws Exception {
        Path filePath = tempDir.resolve("servers.txt");

        String content =
                "PiHole;192.168.188.64;8080;HTTP;/admin/";

        Files.writeString(
                filePath,
                content,
                StandardCharsets.UTF_8
        );

        ServerLoader serverLoader = new ServerLoader();

        List<Server> servers = serverLoader.load(filePath);

        assertEquals(1, servers.size());
        assertEquals("/admin/", servers.get(0).path());
    }
}