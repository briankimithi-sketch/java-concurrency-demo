package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureAllOfDemo {

    public static void main(String[] args) {
        CompletableFuture<String> productFuture =
                CompletableFuture.supplyAsync(
                        () -> "Product: Wireless Headphones");

        CompletableFuture<String> stockFuture =
                CompletableFuture.supplyAsync(
                        () -> "Stock: Available");

        CompletableFuture<String> reviewsFuture =
                CompletableFuture.supplyAsync(
                        () -> "Reviews: 4.5/5");

        CompletableFuture<Void> allTasks =
                CompletableFuture.allOf(
                        productFuture,
                        stockFuture,
                        reviewsFuture);

        allTasks.thenRun(() -> {
            System.out.println(productFuture.join());
            System.out.println(stockFuture.join());
            System.out.println(reviewsFuture.join());
        }).join();
    }
}
