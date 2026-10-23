package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureDemo {

    public static void main(String[] args) {
        CompletableFuture<Double> subtotalFuture =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println(
                            "Calculating subtotal on "
                                    + Thread.currentThread().getName());

                    return 4500.00 * 2;
                });

        CompletableFuture<Double> taxFuture =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println(
                            "Calculating tax on "
                                    + Thread.currentThread().getName());

                    return 4500.00 * 2 * 0.16;
                });

        CompletableFuture<Double> totalFuture =
                subtotalFuture.thenCombine(
                        taxFuture,
                        (subtotal, tax) -> subtotal + tax);

        totalFuture.thenAccept(total ->
                System.out.printf(
                        "Final total: KSh %.2f%n", total))
                .join();
    }
}
