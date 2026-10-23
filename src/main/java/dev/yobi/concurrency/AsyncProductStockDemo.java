package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;

public class AsyncProductStockDemo {

    public static void main(String[] args) {
        CompletableFuture<String> resultFuture =
                CompletableFuture.supplyAsync(
                        () -> "Wireless Headphones")
                .thenCompose(AsyncProductStockDemo::checkStock)
                .exceptionally(error -> "Stock status unavailable");

        System.out.println(resultFuture.join());
    }

    static CompletableFuture<String> checkStock(String product) {
        return CompletableFuture.supplyAsync(() -> {
            if (product == null || product.isBlank()) {
                throw new IllegalArgumentException(
                        "Product name is required");
            }

            if ("Wireless Headphones".equals(product)) {
                return product + " | Stock: Available";
            }

            throw new IllegalStateException(
                    "Stock service is unavailable");
        });
    }
}
