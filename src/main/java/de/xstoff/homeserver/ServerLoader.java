package de.xstoff.homeserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ServerLoader {

    public List<Server> load(Path path) throws IOException {
       
        List<Server> serverList = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);
        
        for (String line : lines) {
            serverList.add(parseServer(line));
        }

        return serverList;
    }

    private Server parseServer(String line) {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                "Invalid server configuration: " + line
            );
        }
        int port;
        try {
            port = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Invalid port: " + parts[2],
                e
            );
        }
        
        return new Server(parts[0], parts[1], port);
    }
}