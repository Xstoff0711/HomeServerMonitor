package de.xstoff.homeserver;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.OptionalLong;

public class HttpServerChecker implements ServerHealthChecker {
    private final Duration timeout;
    public HttpServerChecker(Duration timeout) {
        this.timeout = timeout;
    }
    public ServerCheckResult check(Server server) {
                
        try{
            HttpClient http = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://" + server.ipAddress() + ":" + server.port() + "/"))
                    .timeout(timeout)
                    .build();

            // Start time
            long startTime = System.nanoTime();
            HttpResponse<Void> response = http.send(request, HttpResponse.BodyHandlers.discarding());
            long endTime = System.nanoTime();

            if(response.statusCode() == 200){
                return new ServerCheckResult(true, OptionalLong.of(endTime - startTime));
            }
            else {
                return new ServerCheckResult(false, OptionalLong.empty());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ServerCheckResult(false, OptionalLong.empty());
        } catch (HttpTimeoutException e) {
            return new ServerCheckResult(false, OptionalLong.empty());
        } catch (IOException e) {
            return new ServerCheckResult(false, OptionalLong.empty());
        }
    }
}
