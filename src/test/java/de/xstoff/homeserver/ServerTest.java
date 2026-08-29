package de.xstoff.homeserver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    @Test
    void shouldCreateServer() {
        Server server = new Server("Jellyfin", "192.168.188.72", 8096);
        assertEquals("Jellyfin", server.getName());
        assertEquals("192.168.188.72", server.getIpAddress());
        assertEquals(8096, server.getPort());
    }
}
