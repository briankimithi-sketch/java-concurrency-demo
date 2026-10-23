package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureTimeoutDemo {

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

        String result = stockFuture
                .completeOnTimeout(
                        "Stock service timed out",
                        2,
                        TimeUnit.SECONDS)
                .join();

        System.out.println("Result: " + result);
    }
}
