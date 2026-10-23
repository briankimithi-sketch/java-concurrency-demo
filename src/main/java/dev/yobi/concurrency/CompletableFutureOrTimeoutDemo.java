package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

public class CompletableFutureOrTimeoutDemo {

    public static void main(String[] args) {
        CompletableFuture<String> stockFuture =
                CompletableFuture.supplyAsync(() -> {
                    try {
                        System.out.println(
                                "Contacting stock service...");

                        Thread.sleep(5000);

                        return "Stock available";
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                });

        try {
            String result = stockFuture
                    .orTimeout(2, TimeUnit.SECONDS)
                    .join();

            System.out.println("Result: " + result);

        } catch (CompletionException e) {
            if (e.getCause() instanceof TimeoutException) {
                System.out.println(
                        "Request failed: stock service timed out");
            } else {
                throw e;
            }
        }
    }
}
