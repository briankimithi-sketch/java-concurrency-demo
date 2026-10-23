package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurrentHashMapDemoTest {

    @Test
    void shouldCountConcurrentUpdatesWithoutLosingIncrements()
            throws Exception {

        ConcurrentHashMap<String, Integer> counts =
                new ConcurrentHashMap<>();

        int threadCount = 8;
        int updatesPerThread = 10_000;

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        List<Future<?>> tasks = new ArrayList<>();

        try {
            for (int thread = 0; thread < threadCount; thread++) {
                Future<?> task = executor.submit(() -> {
                    for (int i = 0; i < updatesPerThread; i++) {
                        counts.merge(
                                "Wireless Headphones", 1, Integer::sum);
                        counts.merge("Keyboard", 1, Integer::sum);
                    }
                });

                tasks.add(task);
            }

            executor.shutdown();

            // Surface exceptions thrown by any worker.
            for (Future<?> task : tasks) {
                task.get(5, TimeUnit.SECONDS);
            }

            assertTrue(
                    executor.awaitTermination(5, TimeUnit.SECONDS),
                    "Executor should finish within five seconds"
            );

            int expected = threadCount * updatesPerThread;

            assertEquals(expected, counts.get("Wireless Headphones"));
            assertEquals(expected, counts.get("Keyboard"));

        } finally {
            executor.shutdownNow();
        }
    }
}
