package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomExecutorDemo {

    public static void main(String[] args) {
        AtomicInteger threadNumber = new AtomicInteger(1);

        ExecutorService executor = Executors.newFixedThreadPool(
                3,
                task -> {
                    Thread thread = new Thread(task);
                    thread.setName(
                            "product-worker-" + threadNumber.getAndIncrement()
                    );
                    return thread;
                }
        );

        try {
            CompletableFuture<String> product =
                    CompletableFuture.supplyAsync(() -> {
                        printTask("Product lookup");
                        return "Wireless Headphones";
                    }, executor);

            CompletableFuture<String> stock =
                    CompletableFuture.supplyAsync(() -> {
                        printTask("Stock lookup");
                        return "Available";
                    }, executor);

            CompletableFuture<Integer> price =
                    CompletableFuture.supplyAsync(() -> {
                        printTask("Price lookup");
                        return 4500;
                    }, executor);

            CompletableFuture<String> result = product
                    .thenCombine(stock,
                            (p, s) -> p + " | Stock: " + s)
                    .thenCombine(price,
                            (details, amount) ->
                                    details + " | Price: KSh " + amount);

            System.out.println(result.join());

        } finally {
            executor.shutdown();

            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void printTask(String taskName) {
        System.out.println(
                taskName + " running on "
                        + Thread.currentThread().getName()
        );
    }
}
