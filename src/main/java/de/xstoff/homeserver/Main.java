package de.xstoff.homeserver;

import java.util.ArrayList;
import java.util.List;

public class Main {

    
    public static void main(String[] args) {
        System.out.println("HomeServer Monitor");
        System.out.println("==================");
        
        Server jellyfin = new Server("Jellyfin", "192.168.188.72" ,8096);
        
        Server nextCloud = new Server("NextCloud", "192.168.188.72", 8080);
        Server pihole = new Server("PiHole", "192.168.188.64",8080);

        List<Server> serverList = new ArrayList<>();
            
        serverList.add(pihole);
        serverList.add(nextCloud);
        serverList.add(jellyfin);
        
        ServerChecker serverChecker = new ServerChecker();
        for (Server server : serverList) {
            boolean online = serverChecker.check(server);
            System.out.println(
                server.getName() + " | " + 
                server.getIpAddress()+ ":" + 
                server.getPort()+ " | " + 
                (online ? "ONLINE" : "OFFLINE"));
        }

    }
}