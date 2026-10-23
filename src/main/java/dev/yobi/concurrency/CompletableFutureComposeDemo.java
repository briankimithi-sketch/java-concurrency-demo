package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureComposeDemo {

    public static void main(String[] args) {
        CompletableFuture<String> productFuture =
                CompletableFuture.supplyAsync(
                        () -> "Wireless Headphones");

        CompletableFuture<String> resultFuture =
                productFuture.thenCompose(product ->
                        CompletableFuture.supplyAsync(
                                () -> product + " | Stock: Available"));

        System.out.println(resultFuture.join());
    }
}
