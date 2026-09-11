package de.xstoff.homeserver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;

class ServerCheckerTest {

    @Test
    void shouldReturnFalseWhenPortIsNotReachable() throws IOException {
        ServerChecker checker = new ServerChecker();
        
        int port;
        try (ServerSocket portSocket = new ServerSocket(0)) {
            port = portSocket.getLocalPort();
        }// Close the socket to make the port unreachable
        
        Server server = new Server("TestServer", "127.0.0.1", port);
        ServerCheckResult result = checker.check(server);
        
        assertFalse(result.online());
        assertTrue(result.responseTimeNanos().isEmpty());
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
            assertTrue(result.responseTimeNanos().getAsLong() >= 0);
        }
    }
}