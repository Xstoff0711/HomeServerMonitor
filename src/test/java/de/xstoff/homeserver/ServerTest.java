package de.xstoff.homeserver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    @Test
    void shouldCreateServer() {
        Server server = new Server("Jellyfin", "192.168.188.72", 8096, CheckType.HTTP);
        assertEquals("Jellyfin", server.name());
        assertEquals("192.168.188.72", server.ipAddress());
        assertEquals(8096, server.port());
    }
    @Test
    void shouldConsiderEqualServersAsEqual() {
        // zwei identische Server erzeugen

        Server server1 = new Server("Jellyfin", "192.168.188.72", 8096, CheckType.HTTP);
        Server server2 = new Server("Jellyfin", "192.168.188.72", 8096, CheckType.HTTP);

        // mit assertEquals prüfen
        assertEquals(server1, server2);
    }
}
