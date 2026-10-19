package de.xstoff.homeserver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

class MonitoringServiceTest {

    @Test
    void shouldCheckServersInParallel() throws Exception {
        CountDownLatch httpStarted = new CountDownLatch(1);
        CountDownLatch tcpStarted = new CountDownLatch(1);
        AtomicBoolean ranInParallel = new AtomicBoolean(false);
        CountDownLatch checkFinished = new CountDownLatch(1);

        ServerHealthChecker tcpChecker = server -> {
            tcpStarted.countDown();

            try {
                boolean httpWasStarted =
                        httpStarted.await(1, TimeUnit.SECONDS);

                ranInParallel.set(httpWasStarted);
                checkFinished.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return new ServerCheckResult(
                    true,
                    OptionalLong.of(1)
            );
        };

        ServerHealthChecker httpChecker = server -> {
            try {
                tcpStarted.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            httpStarted.countDown();

            return new ServerCheckResult(
                    true,
                    OptionalLong.of(1)
            );
        };
        List<Server> servers = List.of(
            new Server("TCP-Test", "localhost", 1, CheckType.TCP),
            new Server("HTTP-Test", "localhost", 1, CheckType.HTTP)
        );

        ServerHealthCheckerFactory factory =
                new ServerHealthCheckerFactory(tcpChecker, httpChecker);

        ServerStatusFormatter formatter = new ServerStatusFormatter();

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        ExecutorService checkExecutor =
                Executors.newFixedThreadPool(2);

        MonitoringService monitoringService =
                new MonitoringService(
                        servers,
                        factory,
                        formatter,
                        scheduler,
                        checkExecutor
                );
        try {
            monitoringService.startMonitoring(Duration.ofHours(1));

            boolean finished =
                    checkFinished.await(2, TimeUnit.SECONDS);

            assertTrue(finished);
            assertTrue(ranInParallel.get());
        } finally {
            monitoringService.stopMonitoring();
        }
    }
}