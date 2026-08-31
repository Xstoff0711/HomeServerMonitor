package de.xstoff.homeserver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;

class ServerCheckerTest {

    @Test
    void shouldReturnFalseWhenPortIsNotReachable() {
        ServerChecker checker = new ServerChecker();
        Server server = new Server("TestServer", "127.0.0.1", 1);
        
        ServerCheckResult result = checker.check(server);
        assertTrue(result.responseTimeNanos().isEmpty());
        assertFalse(result.online());
    }

    @Test
    void shouldReturnTrueWhenPortIsReachable() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ServerChecker checker = new ServerChecker();
            Server server = new Server("TestServer", "127.0.0.1", port);

            ServerCheckResult result = checker.check(server);

            assertTrue(result.online());
            assertTrue(result.responseTimeNanos().isPresent());
        }
    }
}