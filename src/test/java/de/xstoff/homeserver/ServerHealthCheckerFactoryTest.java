package de.xstoff.homeserver;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.OptionalLong;

import org.junit.jupiter.api.Test;

class ServerHealthCheckerFactoryTest {
    
    @Test 
    void shouldReturnTcpCheckerForTcpServer(){
        ServerHealthChecker tcpChecker = server -> new ServerCheckResult(false, OptionalLong.empty()); 
        ServerHealthChecker httpChecker = server -> new ServerCheckResult(false, OptionalLong.empty());
        ServerHealthCheckerFactory factory = new ServerHealthCheckerFactory(tcpChecker, httpChecker);
        Server tcpServer = new Server("TestServer", "192.168.1.1", 8080, CheckType.TCP);
        ServerHealthChecker result = factory.getChecker(tcpServer);

        assertSame(tcpChecker, result);
    }
    @Test
    void shouldReturnHttpCheckerForHttpServer(){
        ServerHealthChecker tcpChecker = server -> new ServerCheckResult(false, OptionalLong.empty()); 
        ServerHealthChecker httpChecker = server -> new ServerCheckResult(false, OptionalLong.empty());
        ServerHealthCheckerFactory factory = new ServerHealthCheckerFactory(tcpChecker, httpChecker);
        Server httpServer = new Server("TestServer", "192.168.1.1", 8080, CheckType.HTTP);
        ServerHealthChecker result = factory.getChecker(httpServer);

        assertSame(httpChecker, result);
    }
}
