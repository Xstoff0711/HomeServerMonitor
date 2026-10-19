package de.xstoff.homeserver;

public record ServerStatus(
        Server server,
        ServerCheckResult result
) {
}