package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class CompletableFutureHandleDemo {

    public static void main(String[] args) {
        CompletableFuture<String> stockFuture =
                CompletableFuture.<String>supplyAsync(() -> {
                    throw new IllegalStateException(
                            "Stock service is unavailable");
                });

        CompletableFuture<String> resultFuture =
                stockFuture.handle((stock, error) -> {
                    if (error != null) {
                        Throwable cause =
                                error instanceof CompletionException
                                        && error.getCause() != null
                                        ? error.getCause()
                                        : error;

                        System.out.println(
                                "Recovered from: "
                                + cause.getMessage());

                        return "Stock unavailable";
                    }

                    return stock;
                });

        System.out.println(resultFuture.join());
    }
}
