package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompletableFutureErrorDemoTest {

    @Test
    void exceptionallyShouldRecoverFromFailure() {
        CompletableFuture<String> future =
                CompletableFuture.<String>supplyAsync(() -> {
                    throw new IllegalStateException(
                            "Payment provider is unavailable"
                    );
                }).exceptionally(error -> "PAYMENT_STATUS_UNKNOWN");

        assertEquals("PAYMENT_STATUS_UNKNOWN", future.join());
    }

    @Test
    void handleShouldRecoverFromFailure() {
        CompletableFuture<String> future =
                CompletableFuture.<String>supplyAsync(() -> {
                    throw new IllegalStateException(
                            "Payment provider is unavailable"
                    );
                }).handle((result, error) -> {
                    if (error != null) {
                        return "MANUAL_REVIEW_REQUIRED";
                    }
                    return result;
                });

        assertEquals("MANUAL_REVIEW_REQUIRED", future.join());
    }

    @Test
    void unhandledFailureShouldRemainExceptional() {
        CompletableFuture<String> future =
                CompletableFuture.<String>supplyAsync(() -> {
                    throw new IllegalStateException("Simulated failure");
                });

        assertThrows(CompletionException.class, future::join);
    }
}
