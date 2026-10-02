package de.xstoff.homeserver;

public interface ServerHealthChecker {
    ServerCheckResult check(Server server);
}
