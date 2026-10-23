package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParallelProductLookupDemoTest {

    @Test
    void combinesProductStockAndPrice() {
        CompletableFuture<String> product =
                CompletableFuture.completedFuture(
                        "Wireless Headphones");

        CompletableFuture<String> stock =
                CompletableFuture.completedFuture("Available");

        CompletableFuture<Integer> price =
                CompletableFuture.completedFuture(4500);

        CompletableFuture<String> result =
                product.thenCombine(stock,
                        (name, availability) ->
                                name + " | Stock: " + availability)
                .thenCombine(price,
                        (details, amount) ->
                                details + " | Price: KSh " + amount);

        assertEquals(
                "Wireless Headphones | Stock: Available | Price: KSh 4500",
                result.join());
    }

    @Test
    void preservesProductAndStockWhenPriceIsUnavailable() {
        CompletableFuture<String> product =
                CompletableFuture.completedFuture(
                        "Wireless Headphones");

        CompletableFuture<String> stock =
                CompletableFuture.completedFuture("Available");

        CompletableFuture<Integer> price =
                CompletableFuture.<Integer>failedFuture(
                        new IllegalStateException(
                                "Price service unavailable"))
                .exceptionally(error -> null);

        CompletableFuture<String> result =
                product.thenCombine(stock,
                        (name, availability) ->
                                name + " | Stock: " + availability)
                .thenCombine(price,
                        (details, amount) -> {
                            if (amount == null) {
                                return details
                                        + " | Price: Temporarily unavailable";
                            }

                            return details + " | Price: KSh " + amount;
                        });

        assertEquals(
                "Wireless Headphones | Stock: Available | Price: Temporarily unavailable",
                result.join());
    }
}
