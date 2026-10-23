package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

class CompletableFutureAnyOfDemoTest {

    @Test
    void returnsResultWhenFirstFutureCompletes() {
        CompletableFuture<String> fast =
                CompletableFuture.completedFuture("Fast");

        CompletableFuture<String> slow =
                new CompletableFuture<>();

        CompletableFuture<Object> result =
                CompletableFuture.anyOf(fast, slow);

        assertEquals("Fast", result.join());
        assertFalse(slow.isDone());
    }

    @Test
    void completesExceptionallyWhenFirstFutureFails() {
        CompletableFuture<String> failed =
                CompletableFuture.failedFuture(
                        new IllegalStateException("Service failed"));

        CompletableFuture<String> pending =
                new CompletableFuture<>();

        CompletableFuture<Object> result =
                CompletableFuture.anyOf(failed, pending);

        CompletionException exception =
                assertThrows(
                        CompletionException.class,
                        result::join);

        assertInstanceOf(
                IllegalStateException.class,
                exception.getCause());

        assertEquals(
                "Service failed",
                exception.getCause().getMessage());
    }
}
