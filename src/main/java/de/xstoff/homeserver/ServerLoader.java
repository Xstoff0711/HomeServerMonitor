package de.xstoff.homeserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

    public List<Server> loadResource(String resourceName) throws IOException {

        try (InputStream inputStream = ServerLoader.class.getClassLoader().getResourceAsStream(resourceName)) {

            if (inputStream == null) {
                throw new IllegalArgumentException(
                        "Resource not found: " + resourceName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                List<Server> serverList = new ArrayList<>();
                String line;

                while ((line = reader.readLine()) != null) {
                    Optional<Server> server = parseLine(line);
                    server.ifPresent(serverList::add);
                }
                return serverList;
            }
        }
    }

    private Server parseServer(String line) {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid server configuration: " + line);
        }
        int port;
        try {
            port = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid port: " + parts[2],
                    e);
        }

        return new Server(parts[0], parts[1], port);
    }
}