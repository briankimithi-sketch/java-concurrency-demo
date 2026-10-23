package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrencyTimingDemo {

    private static String lookup(String name) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    name + " lookup interrupted", e);
        }

        return name + " completed";
    }

    public static void main(String[] args) {
        long sequentialStart = System.nanoTime();

        System.out.println(lookup("Product"));
        System.out.println(lookup("Stock"));
        System.out.println(lookup("Price"));

        long sequentialMs = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - sequentialStart);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            long concurrentStart = System.nanoTime();

            CompletableFuture<String> product =
                    CompletableFuture.supplyAsync(
                            () -> lookup("Product"), executor);

            CompletableFuture<String> stock =
                    CompletableFuture.supplyAsync(
                            () -> lookup("Stock"), executor);

            CompletableFuture<String> price =
                    CompletableFuture.supplyAsync(
                            () -> lookup("Price"), executor);

            CompletableFuture.allOf(
                    product, stock, price).join();

            long concurrentMs = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - concurrentStart);

            System.out.println("\nSequential time: "
                    + sequentialMs + " ms");
            System.out.println("Concurrent time: "
                    + concurrentMs + " ms");

            System.out.printf("Speedup: %.2fx%n",
                    (double) sequentialMs / concurrentMs);

        } finally {
            executor.shutdown();
        }
    }
}
