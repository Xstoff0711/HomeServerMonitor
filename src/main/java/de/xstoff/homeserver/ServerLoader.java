package de.xstoff.homeserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServerLoader {

    private Optional<Server> parseLine(String line) {
        String trimmedLine = line.strip();

        if (trimmedLine.isBlank() || trimmedLine.startsWith("#")) {
            return Optional.empty();
        }

        return Optional.of(parseServer(trimmedLine));
    }

    public List<Server> load(Path path) throws IOException {

        List<Server> serverList = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);

        for (String line : lines) {
            Optional<Server> server = parseLine(line);
            server.ifPresent(serverList::add);
        }

        return serverList;
    }
    
    private Server parseServer(String line) {
        String[] parts = line.split(";");
        if (parts.length != 4 && parts.length != 5) {
            throw new IllegalArgumentException(
                    "Invalid server configuration: " + line);
        }

        String path = parts.length == 5 ? parts[4].strip() : "/";

        int port;
        try {
            port = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid port: " + parts[2],
                    e);
        }

        CheckType checkType;
        switch (parts[3].strip().toLowerCase()) {
            case "tcp":
                checkType = CheckType.TCP;
                break;
            case "http":
                checkType = CheckType.HTTP;
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid check type: " + parts[3]);
        }
        String[] pathList = line.split(";");
        if (pathList.length != 4 && pathList.length != 5) {
            throw new IllegalArgumentException(
                    "Invalid server configuration: " + line);
        }

        return new Server(parts[0], parts[1], port, checkType , path);
    }
}