package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureAnyOfDemo {

    public static void main(String[] args) {
        CompletableFuture<String> fastService =
                CompletableFuture.supplyAsync(() -> {
                    sleep(500);
                    return "Fast service responded";
                });

        CompletableFuture<String> mediumService =
                CompletableFuture.supplyAsync(() -> {
                    sleep(1500);
                    return "Medium service responded";
                });

        CompletableFuture<String> slowService =
                CompletableFuture.supplyAsync(() -> {
                    sleep(3000);
                    return "Slow service responded";
                });

        CompletableFuture<Object> firstResponse =
                CompletableFuture.anyOf(
                        fastService,
                        mediumService,
                        slowService);

        System.out.println(firstResponse.join());
    }

    private static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Task was interrupted", e);
        }
    }
}
