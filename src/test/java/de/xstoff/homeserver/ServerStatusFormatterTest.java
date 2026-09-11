package de.xstoff.homeserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.OptionalLong;

import org.junit.jupiter.api.Test;

public class ServerStatusFormatterTest {
    
    @Test
    void shouldFormatOnlineServer(){
        Server server = new Server("Jellyfin", "192.168.188.72", 8096);
        ServerCheckResult result = new ServerCheckResult(true, OptionalLong.of(1500000 )); // 1.5 ms in nanoseconds
        ServerStatusFormatter formatter = new ServerStatusFormatter();
        String formattedStatus = formatter.format(server, result);
        String expectedStatus = "Jellyfin | 192.168.188.72:8096 | ONLINE | 1.50 ms"; 
        
        assertEquals(expectedStatus, formattedStatus);
    }

    @Test
    void shouldFormatOfflineServer() {
        Server server = new Server("Jellyfin", "192.168.188.72", 8096);
        ServerCheckResult result = new ServerCheckResult(false,OptionalLong.empty()); // 5 ms in nanoseconds
        ServerStatusFormatter formatter = new ServerStatusFormatter();
        String formattedStatus = formatter.format(server, result);
        String expectedStatus = "Jellyfin | 192.168.188.72:8096 | OFFLINE | -"; 
        
        assertEquals(expectedStatus, formattedStatus);
    }
}
