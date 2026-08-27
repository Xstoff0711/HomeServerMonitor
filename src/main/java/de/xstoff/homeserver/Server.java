package de.xstoff.homeserver;

public class Server {

    private String name;
    private String ipAddress;
    private int port;

    public Server(String name, String ipAddress, int port){
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
    }
    public String getName() {
        return name;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public int getPort() {
        return port;
    }

}
