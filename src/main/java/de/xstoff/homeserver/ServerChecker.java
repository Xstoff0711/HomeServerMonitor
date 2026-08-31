package de.xstoff.homeserver;

import java.net.Socket;
import java.util.OptionalLong;
import java.net.InetSocketAddress;
import java.io.IOException;

public class ServerChecker {
    
    public ServerCheckResult check(Server server) {
        try (Socket socket = new Socket()){
            long start = System.nanoTime();
            socket.connect(new InetSocketAddress(server.ipAddress(), server.port()), 2000);
            long end = System.nanoTime();
            long durationNanos = end - start;
           
            return new ServerCheckResult(true, OptionalLong.of(durationNanos));
        } catch (IOException e) {
            return new ServerCheckResult(false, OptionalLong.empty())   ;
        }
    }
}
