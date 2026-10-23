package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FutureTimeoutDemoTest {

    @Test
    void shouldTimeoutAndCancelSlowTask() throws Exception {
        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        CountDownLatch started = new CountDownLatch(1);

        try {
            Future<String> future = executor.submit(() -> {
                started.countDown();

                try {
                    new CountDownLatch(1).await();
                    return "Completed";
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            });

            assertTrue(started.await(2, TimeUnit.SECONDS));

            assertThrows(
                    TimeoutException.class,
                    () -> future.get(100, TimeUnit.MILLISECONDS));

            assertTrue(future.cancel(true));
            assertTrue(future.isCancelled());

        } finally {
            executor.shutdownNow();
        }
    }
}
