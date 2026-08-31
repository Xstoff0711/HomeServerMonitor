package de.xstoff.homeserver;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("HomeServer Monitor");
        System.out.println("==================");

        List<Server> serverList;

        ServerLoader serverLoader = new ServerLoader();
        try {
            serverList = serverLoader.loadResource("servers.txt");
        } catch (IOException e) {
            System.err.println("Serverliste konnte nicht geladen werden.");
            e.printStackTrace();
            return;
        }

        ServerChecker serverChecker = new ServerChecker();
        for (Server server : serverList) {
            printServerStatus(server, serverChecker);
        }

    }

    private static void printServerStatus(Server server, ServerChecker serverChecker) {
        ServerCheckResult result = serverChecker.check(server);
        double responseTimeMillis = result.responseTimeNanos().getAsLong() / 1_000_000.0;
        System.out.println(
                server.name() + " | " +
                        server.ipAddress() + ":" +
                        server.port() + " | " +
                        (result.online() ? "ONLINE" : "OFFLINE") + " | " +
                        String.format("%.2f", responseTimeMillis) + " ms");

    }
}