package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompletableFutureCompositionTest {

    @Test
    void thenApplyCanProduceNestedFuture() {
        CompletableFuture<String> productFuture =
                CompletableFuture.completedFuture("Wireless Headphones");

        CompletableFuture<CompletableFuture<String>> nestedFuture =
                productFuture.thenApply(product ->
                        CompletableFuture.completedFuture(
                                product + " | Stock: Available"));

        assertEquals(
                "Wireless Headphones | Stock: Available",
                nestedFuture.join().join());
    }

    @Test
    void thenComposeFlattensNestedFuture() {
        CompletableFuture<String> productFuture =
                CompletableFuture.completedFuture("Wireless Headphones");

        CompletableFuture<String> resultFuture =
                productFuture.thenCompose(product ->
                        CompletableFuture.completedFuture(
                                product + " | Stock: Available"));

        assertEquals(
                "Wireless Headphones | Stock: Available",
                resultFuture.join());
    }
}
