package de.xstoff.homeserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class StatusApiServer {
    private final HttpServer httpServer;
    private final MonitoringService monitoringService;
    private final ServerLoader serverLoader;
    private final Path configPath;

    public StatusApiServer(
            int port,
            MonitoringService monitoringService,
            ServerLoader serverLoader,
            Path configPath) throws IOException {

        this.monitoringService = monitoringService;
        this.serverLoader = serverLoader;
        this.configPath = configPath;

        this.httpServer = HttpServer.create(
                new InetSocketAddress("127.0.0.1", port),
                0);
    }

    public void start() {
        httpServer.createContext("/status", exchange -> {
            List<ServerStatus> statuses = monitoringService.getCurrentStatuses();

            String response = buildStatusJson(statuses);

            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/json; charset=utf-8");

            exchange.sendResponseHeaders(
                    200,
                    responseBytes.length);

            exchange.getResponseBody().write(responseBytes);
            exchange.close();
        });
        httpServer.createContext("/servers", this::handleServers);
        httpServer.start();
    }

    private String buildStatusJson(List<ServerStatus> statuses) {
        StringBuilder json = new StringBuilder();

        json.append("[");

        for (int i = 0; i < statuses.size(); i++) {
            ServerStatus status = statuses.get(i);

            Server server = status.server();
            ServerCheckResult result = status.result();

            json.append("{");
            json.append("\"name\":\"").append(server.name()).append("\",");
            json.append("\"online\":").append(result.online()).append(",");
            json.append("\"responseTimeMs\":");

            if (result.responseTimeNanos().isPresent()) {
                double responseTimeMs = result.responseTimeNanos().getAsLong()
                        / 1_000_000.0;

                json.append(
                        String.format(
                                Locale.US,
                                "%.2f",
                                responseTimeMs));
            } else {
                json.append("null");
            }

            json.append("}");

            if (i < statuses.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        return json.toString();
    }

    private void handleServers(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);

        Map<String, String> values = parseForm(body);

        String name = values.getOrDefault("name", "").trim();
        String host = values.getOrDefault("host", "").trim();
        String portValue = values.getOrDefault("port", "").trim();
        String checkTypeValue = values
                .getOrDefault("checkType", "")
                .trim()
                .toUpperCase();

        String path = values
                .getOrDefault("path", "/")
                .trim();

        if (name.isBlank() || host.isBlank()) {
            sendResponse(exchange, 400, "Name and host are required");
            return;
        }

        if (containsInvalidCharacters(name)
                || containsInvalidCharacters(host)
                || containsInvalidCharacters(path)) {

            sendResponse(exchange, 400, "Invalid characters");
            return;
        }

        int port;

        try {
            port = Integer.parseInt(portValue);
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid port");
            return;
        }

        if (port < 1 || port > 65535) {
            sendResponse(exchange, 400, "Port must be between 1 and 65535");
            return;
        }

        CheckType checkType;

        try {
            checkType = CheckType.valueOf(checkTypeValue);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid check type");
            return;
        }

        Server newServer;

        if (checkType == CheckType.HTTP) {
            if (path.isBlank()) {
                path = "/";
            }

            if (!path.startsWith("/")) {
                sendResponse(exchange, 400, "HTTP path must start with /");
                return;
            }

            newServer = new Server(
                    name,
                    host,
                    port,
                    checkType,
                    path);
        } else {
            newServer = new Server(
                    name,
                    host,
                    port,
                    checkType);
        }

        if (monitoringService.containsServer(newServer)) {
            sendResponse(exchange, 409, "Server already exists");
            return;
        }

        serverLoader.append(configPath, newServer);
        monitoringService.addServer(newServer);

        sendResponse(exchange, 201, "Server added");
    }

    private Map<String, String> parseForm(String body) {
        Map<String, String> values = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            if (parts.length == 2) {
                String key = URLDecoder.decode(
                        parts[0],
                        StandardCharsets.UTF_8);

                String value = URLDecoder.decode(
                        parts[1],
                        StandardCharsets.UTF_8);

                values.put(key, value);
            }
        }

        return values;
    }

    private boolean containsInvalidCharacters(String value) {
        return value.contains(";")
                || value.contains("\n")
                || value.contains("\r");
    }

    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String message) throws IOException {

        byte[] response = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=utf-8");

        exchange.sendResponseHeaders(
                statusCode,
                response.length);

        try (OutputStream output = exchange.getResponseBody()) {

            output.write(response);
        }
    }
}