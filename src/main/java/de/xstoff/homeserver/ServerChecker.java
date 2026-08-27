package de.xstoff.homeserver;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.IOException;

public class ServerChecker {
    
    public boolean check(Server server) {
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(server.getIpAddress(), server.getPort()), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
