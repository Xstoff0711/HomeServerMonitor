package de.xstoff.homeserver;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {
    
    
    public static void main(String[] args) {
        System.out.println("HomeServer Monitor");
        System.out.println("==================");
        
        List<Server> serverList;
        
        ServerLoader serverLoader = new ServerLoader();
        try {
            Path path = Path.of("src/main/resources/servers.txt");
            serverList = serverLoader.load(path);
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
        boolean online = serverChecker.check(server);

        System.out.println(
        server.name() + " | " + 
        server.ipAddress()+ ":" + 
        server.port()+ " | " + 
        (online ? "ONLINE" : "OFFLINE"));

    }
}