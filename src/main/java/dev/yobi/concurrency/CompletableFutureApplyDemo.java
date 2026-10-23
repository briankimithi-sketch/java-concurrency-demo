package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureApplyDemo {

    public static void main(String[] args) {
        CompletableFuture<String> productFuture =
                CompletableFuture.supplyAsync(
                        () -> "Wireless Headphones");

        CompletableFuture<CompletableFuture<String>> nestedFuture =
                productFuture.thenApply(product ->
                        CompletableFuture.supplyAsync(
                                () -> product + " | Stock: Available"));

        System.out.println(
                nestedFuture.join().join());
    }
}
