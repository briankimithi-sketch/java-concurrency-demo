package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompletableFutureTimeoutMethodsTest {

    @Test
    void completeOnTimeoutReturnsFallbackValue() {
        CompletableFuture<String> future =
                new CompletableFuture<>();

        String result = future.completeOnTimeout(
                "Stock unavailable",
                50,
                TimeUnit.MILLISECONDS
        ).join();

        assertEquals("Stock unavailable", result);
    }

    @Test
    void orTimeoutCompletesExceptionally() {
        CompletableFuture<String> future =
                new CompletableFuture<>();

        CompletionException exception = assertThrows(
                CompletionException.class,
                () -> future.orTimeout(
                        50,
                        TimeUnit.MILLISECONDS
                ).join()
        );

        assertInstanceOf(
                TimeoutException.class,
                exception.getCause()
        );
    }
}
