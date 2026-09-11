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
        ServerStatusFormatter formatter = new ServerStatusFormatter();
        for (Server server : serverList) {
            printServerStatus(server, serverChecker, formatter);
        }
        
    }
    
    private static void printServerStatus(Server server, ServerChecker serverChecker, ServerStatusFormatter formatter) {
        ServerCheckResult result = serverChecker.check(server);
        System.out.println(formatter.format(server, result));
    }
}