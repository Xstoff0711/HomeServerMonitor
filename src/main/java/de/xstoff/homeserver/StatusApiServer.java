package de.xstoff.homeserver;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class StatusApiServer {

    private final HttpServer server;
    private final MonitoringService monitoringService;
    public StatusApiServer(int port, MonitoringService monitoringService) throws IOException {
        this.server = HttpServer.create( new InetSocketAddress("localhost", port),0);
        this.monitoringService = monitoringService;
    }

    public void start() {
        server.createContext("/status", exchange -> {
            List<ServerStatus> statuses =
                    monitoringService.getCurrentStatuses();

            String response = buildStatusJson(statuses);

            byte[] responseBytes =
                    response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/json; charset=utf-8"
            );

            exchange.sendResponseHeaders(
                    200,
                    responseBytes.length
            );

            exchange.getResponseBody().write(responseBytes);
            exchange.close();
        });

        server.start();
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
                double responseTimeMs =
                        result.responseTimeNanos().getAsLong()
                                / 1_000_000.0;

                json.append(
                        String.format(
                                Locale.US,
                                "%.2f",
                                responseTimeMs
                        )
                );
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

}