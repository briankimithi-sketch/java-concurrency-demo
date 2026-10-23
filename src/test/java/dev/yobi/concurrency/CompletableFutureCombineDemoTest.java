package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompletableFutureCombineDemoTest {

    @Test
    void combinesProductAndPrice() {
        CompletableFuture<String> product =
                CompletableFuture.completedFuture("Wireless Headphones");

        CompletableFuture<Integer> price =
                CompletableFuture.completedFuture(4500);

        CompletableFuture<String> result =
                product.thenCombine(
                        price,
                        (name, amount) ->
                                name + " | Price: KSh " + amount);

        assertEquals(
                "Wireless Headphones | Price: KSh 4500",
                result.join());
    }

    @Test
    void combinedFutureFailsWhenOneTaskFails() {
        CompletableFuture<String> product =
                CompletableFuture.completedFuture("Wireless Headphones");

        CompletableFuture<Integer> price =
                CompletableFuture.failedFuture(
                        new IllegalStateException("Price service unavailable"));

        CompletableFuture<String> result =
                product.thenCombine(
                        price,
                        (name, amount) ->
                                name + " | Price: KSh " + amount);

        CompletionException exception =
                assertThrows(CompletionException.class, result::join);

        assertEquals(
                "Price service unavailable",
                exception.getCause().getMessage());
    }
}
