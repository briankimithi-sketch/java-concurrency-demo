package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureRecoveryDemo {

    public static void main(String[] args) {
        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        try {
            CompletableFuture<String> productFuture =
                    CompletableFuture.supplyAsync(
                            () -> "Wireless Headphones",
                            executor);

            CompletableFuture<String> stockFuture =
                    CompletableFuture.<String>supplyAsync(() -> {
                        throw new IllegalStateException(
                                "Stock service is unavailable");
                    }, executor).exceptionally(error -> {
                        Throwable cause = error.getCause() != null
                                ? error.getCause()
                                : error;

                        System.out.println(
                                "Stock lookup failed: "
                                + cause.getMessage());

                        return "Stock temporarily unavailable";
                    });

            CompletableFuture<String> summaryFuture =
                    productFuture.thenCombine(
                            stockFuture,
                            (product, stock) ->
                                    product + " | Stock: " + stock);

            System.out.println(summaryFuture.join());

        } finally {
            executor.shutdown();
        }
    }
}
