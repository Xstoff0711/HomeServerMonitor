package de.xstoff.homeserver;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonitoringService {
    private final CopyOnWriteArrayList<Server> servers;
    private final ServerHealthCheckerFactory checkerFactory;
    private final ServerStatusFormatter formatter;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService checkExecutor;
    private final Map<Server, ServerCheckResult> currentStatuses = new ConcurrentHashMap<>();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public MonitoringService(List<Server> servers, ServerHealthCheckerFactory checkerFactory,
            ServerStatusFormatter formatter, ScheduledExecutorService scheduler, ExecutorService checkExecutor) {
        this.servers = new CopyOnWriteArrayList<>(servers);
        this.checkerFactory = checkerFactory;
        this.formatter = formatter;
        this.scheduler = scheduler;
        this.checkExecutor = checkExecutor;
    }

    private void checkAllServers() {
        List<Future<ServerStatus>> futures = new ArrayList<>();
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        System.out.println();
        System.out.println("--- Check " + timestamp + " ---");
        for (Server server : servers) {
            Future<ServerStatus> future = checkExecutor.submit(() -> checkServer(server));

            futures.add(future);
        }

        for (Future<ServerStatus> future : futures) {
            try {
                ServerStatus status = future.get();

                currentStatuses.put(
                        status.server(),
                        status.result());

                System.out.println(
                        formatter.format(
                                status.server(),
                                status.result()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (ExecutionException e) {
                System.err.println(
                        "Fehler beim Servercheck: " + e.getCause());
            }
        }
    }

    public void startMonitoring(Duration interval) {
        scheduler.scheduleWithFixedDelay(
                this::checkAllServers,
                0,
                interval.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    public void stopMonitoring() {
        scheduler.shutdown();
        checkExecutor.shutdown();
    }

    public List<ServerStatus> getCurrentStatuses() {
        return servers.stream()
                .filter(currentStatuses::containsKey)
                .map(server -> new ServerStatus(
                        server,
                        currentStatuses.get(server)))
                .toList();
    }

    private ServerStatus checkServer(Server server) {
        ServerHealthChecker checker = checkerFactory.getChecker(server);

        ServerCheckResult result = checker.check(server);

        if (servers.contains(server)) {
            currentStatuses.put(server, result);
        }

        return new ServerStatus(server, result);
    }

    public boolean addServer(Server server) {
        boolean added = servers.addIfAbsent(server);

        if (added) {
            checkExecutor.submit(() -> checkServer(server));
        }

        return added;
    }

    public boolean containsServer(Server server) {
        return servers.contains(server);
    }

    public boolean removeServer(Server server) {
        boolean removed = servers.remove(server);

        if (removed) {
            currentStatuses.remove(server);
        }

        return removed;
    }
}
