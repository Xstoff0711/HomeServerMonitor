package de.xstoff.homeserver;

public record Server(
        String name,
        String ipAddress,
        int port,
        CheckType checkType,
        String path
) {
    public Server(
            String name,
            String ipAddress,
            int port,
            CheckType checkType
    ) {
        this(name, ipAddress, port, checkType, "/");
    }
}