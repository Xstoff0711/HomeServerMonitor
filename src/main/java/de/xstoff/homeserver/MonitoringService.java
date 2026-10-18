package de.xstoff.homeserver;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.Duration;

public class MonitoringService {
    private final List<Server> servers;
    private final ServerHealthCheckerFactory checkerFactory;
    private final ServerStatusFormatter formatter;
    private final ScheduledExecutorService scheduler;

    public MonitoringService(List<Server> servers, ServerHealthCheckerFactory checkerFactory, ServerStatusFormatter formatter, ScheduledExecutorService scheduler) {
        this.servers = servers;
        this.checkerFactory = checkerFactory;
        this.formatter = formatter;
        this.scheduler = scheduler;
    }

    private void checkAllServers() {
        for (Server server : servers) {
            ServerHealthChecker checker = checkerFactory.getChecker(server);
            ServerCheckResult result = checker.check(server);
            System.out.println(formatter.format(server, result));
        }
    }

    public void startMonitoring(Duration interval) {
        scheduler.scheduleWithFixedDelay(
            this::checkAllServers,
            0,
            interval.toMillis(),
            TimeUnit.MILLISECONDS);
    }
}
