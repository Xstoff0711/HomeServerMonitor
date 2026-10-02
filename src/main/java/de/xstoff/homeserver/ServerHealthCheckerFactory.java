package de.xstoff.homeserver;

public class ServerHealthCheckerFactory {
    private final ServerHealthChecker tcpChecker;
    private final ServerHealthChecker httpChecker;
    public ServerHealthCheckerFactory(ServerHealthChecker tcpChecker, ServerHealthChecker httpChecker) {
        this.tcpChecker = tcpChecker;
        this.httpChecker = httpChecker;
    }
    public ServerHealthChecker getChecker(Server server) {
        return switch (server.checkType()) {
            case TCP -> tcpChecker;
            case HTTP -> httpChecker;
        };
    }
}
