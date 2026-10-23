package dev.yobi.concurrency;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ParallelProductLookupDemo {

    public static void main(String[] args) {
        CompletableFuture<String> productFuture =
                CompletableFuture.supplyAsync(
                        () -> "Wireless Headphones");

        CompletableFuture<String> stockFuture =
                CompletableFuture.supplyAsync(
                        () -> "Available");

        CompletableFuture<Optional<Integer>> priceFuture =
                CompletableFuture.<Optional<Integer>>supplyAsync(() -> {
                    throw new IllegalStateException(
                            "Price service unavailable");
                })
                .exceptionally(error -> Optional.empty());

        CompletableFuture<String> resultFuture =
                productFuture.thenCombine(
                        stockFuture,
                        (product, stock) ->
                                product + " | Stock: " + stock)
                .thenCombine(
                        priceFuture,
                        (details, price) ->
                                details + " | Price: "
                                + price.map(amount -> "KSh " + amount)
                                       .orElse("Temporarily unavailable"));

        System.out.println(resultFuture.join());
    }
}
