package de.xstoff.homeserver;

import java.io.IOException;
import java.time.Duration;
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
        
        ServerHealthChecker tcpChecker = new ServerChecker();
        ServerHealthChecker httpChecker = new HttpServerChecker(Duration.ofSeconds(2));
        
        ServerHealthCheckerFactory factory = new ServerHealthCheckerFactory(tcpChecker, httpChecker);
        ServerStatusFormatter formatter = new ServerStatusFormatter();
        for (Server server : serverList) {
            ServerHealthChecker checker = factory.getChecker(server);
            printServerStatus(server, checker, formatter);
        }
        
    }
    
    private static void printServerStatus(Server server, ServerHealthChecker serverChecker, ServerStatusFormatter formatter) {
        ServerCheckResult result = serverChecker.check(server);
        System.out.println(formatter.format(server, result));
    }
}