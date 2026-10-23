package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FutureExceptionDemoTest {

    @Test
    void shouldExposeOriginalCauseWhenTaskFails() {
        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        try {
            Future<String> future = executor.submit(() -> {
                throw new IllegalStateException(
                        "Payment provider is unavailable");
            });

            ExecutionException exception =
                    assertThrows(
                            ExecutionException.class,
                            future::get);

            assertEquals(
                    IllegalStateException.class,
                    exception.getCause().getClass());

            assertEquals(
                    "Payment provider is unavailable",
                    exception.getCause().getMessage());

        } finally {
            executor.shutdownNow();
        }
    }
}
