package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RaceConditionDemoTest {

    @Test
    void concurrentIncrementsShouldNotLoseUpdates()
            throws Exception {

        int threadCount = 8;
        int incrementsPerThread = 100_000;
        int expected = threadCount * incrementsPerThread;

        RaceConditionDemo.UnsafeCounter counter =
                new RaceConditionDemo.UnsafeCounter();

        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        try {
            List<Future<?>> tasks = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                tasks.add(executor.submit(() -> {
                    start.await();

                    for (int j = 0; j < incrementsPerThread; j++) {
                        counter.increment();
                    }

                    return null;
                }));
            }

            start.countDown();

            for (Future<?> task : tasks) {
                task.get();
            }

            assertEquals(expected, counter.getCount(),
                    "Concurrent increments must not be lost");
        } finally {
            executor.shutdownNow();
        }
    }
}
