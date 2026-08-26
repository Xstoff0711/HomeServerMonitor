package de.xstoff.homeserver;

public class Main {

    
    public static void main(String[] args) {
        System.out.println("HomeServer Monitor");
        System.out.println("==================");

        Server jellyfin = new Server("Jellyfin", "192.168.188.72" ,8096,"ONLINE");
        System.out.println(jellyfin.getName() + "// " + jellyfin.getIpAddress()+ "// " + jellyfin.getPort()+ "// " + jellyfin.getStatus());
    }
}