package de.xstoff.homeserver;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("HomeServer Monitor");
        System.out.println("==================");

        List<Server> serverList;

        ServerLoader serverLoader = new ServerLoader();
        try {
            serverList = serverLoader.load(ConfigPaths.getServersConfigPath());
        } catch (IOException e) {
            System.err.println("Serverliste konnte nicht geladen werden.");
            e.printStackTrace();
            return;
        }

        ServerHealthChecker tcpChecker = new ServerChecker();
        ServerHealthChecker httpChecker = new HttpServerChecker(Duration.ofSeconds(2));

        ServerHealthCheckerFactory factory = new ServerHealthCheckerFactory(tcpChecker, httpChecker);
        ServerStatusFormatter formatter = new ServerStatusFormatter();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ExecutorService checkExecutor = Executors.newFixedThreadPool(4);
        MonitoringService monitoringService = new MonitoringService(
                serverList,
                factory,
                formatter,
                scheduler,
                checkExecutor);

        StatusApiServer apiServer = new StatusApiServer(
                8085,
                monitoringService,
                serverLoader,
                ConfigPaths.getServersConfigPath());
        apiServer.start();
        Runtime.getRuntime().addShutdownHook(
                new Thread(monitoringService::stopMonitoring));
        monitoringService.startMonitoring(Duration.ofSeconds(30));
    }
}