package de.xstoff.homeserver;

public class ServerStatusFormatter {
    public String format(Server server, ServerCheckResult result) {
        String responseTimeStr = result.responseTimeNanos().isPresent() 
                ? String.format("%.2f ms", result.responseTimeNanos().getAsLong() / 1_000_000.0) 
                : "-";
        
        return String.format("%s | %s:%d | %s | %s",
                server.name(),
                server.ipAddress(),
                server.port(),
                result.online() ? "ONLINE" : "OFFLINE",
                responseTimeStr);
    }
}
