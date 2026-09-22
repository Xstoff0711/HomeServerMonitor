package de.xstoff.homeserver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import com.sun.net.httpserver.HttpServer;

public class HttpServerCheckerTest {
    @Test
    void shouldReturnOnlineForHttp200() throws IOException{
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);

        try{
        server.createContext("/", exchange -> {
            byte[] response = "OK".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        HttpServerChecker checker = new HttpServerChecker();
        Server testServer = new Server("TestServer", "localhost", server.getAddress().getPort());
        ServerCheckResult result = checker.check(testServer, Duration.ofMillis(100));
        assertTrue(result.online());
        assertTrue(result.responseTimeNanos().isPresent());
        assertTrue(result.responseTimeNanos().getAsLong() >= 0);
        
        }
        finally {
            server.stop(0);
        }
    }

    @Test
    void shouldReturnOfflineForHttp404() throws IOException{
         HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);

        try{
        server.createContext("/", exchange -> {
            byte[] response = "OK".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        HttpServerChecker checker = new HttpServerChecker();
        Server testServer = new Server("TestServer", "localhost", server.getAddress().getPort());
        ServerCheckResult result = checker.check(testServer, Duration.ofMillis(100));
        assertFalse(result.online());
        assertTrue(result.responseTimeNanos().isEmpty());
        }
        finally {
            server.stop(0);
        }
    }
    @Test
    void shouldReturnOfflineWhenHttpRequestTimesOut() throws IOException {
         HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);

        try {
            
            server.createContext("/", exchange -> {
                byte[] response = "OK".getBytes(StandardCharsets.UTF_8);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            });
            server.start();

            HttpServerChecker checker = new HttpServerChecker(); // 100ms Timeout
            ServerCheckResult result = checker.check(new Server("TestServer", "localhost", server.getAddress().getPort()), Duration.ofMillis(100));

            assertFalse(result.online());
            assertTrue(result.responseTimeNanos().isEmpty());
        }
        finally {
            server.stop(0);
        }
    }
}
