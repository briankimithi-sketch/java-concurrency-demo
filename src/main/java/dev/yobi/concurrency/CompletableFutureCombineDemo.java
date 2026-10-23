package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureCombineDemo {

    public static void main(String[] args) {
        CompletableFuture<String> productFuture =
                CompletableFuture.supplyAsync(
                        () -> "Wireless Headphones");

        CompletableFuture<Integer> priceFuture =
                CompletableFuture.supplyAsync(
                        () -> 4500);

        CompletableFuture<String> summaryFuture =
                productFuture.thenCombine(
                        priceFuture,
                        (product, price) ->
                                product + " | Price: KSh " + price);

        System.out.println(summaryFuture.join());
    }
}
