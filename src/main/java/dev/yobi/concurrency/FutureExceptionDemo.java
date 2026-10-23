package dev.yobi.concurrency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureExceptionDemo {

    public static void main(String[] args) {
        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        try {
            Future<String> paymentFuture = executor.submit(() -> {
                System.out.println(
                        "Processing payment on "
                                + Thread.currentThread().getName());

                // Simulate a payment failure.
                throw new IllegalStateException(
                        "Payment provider is unavailable");
            });

            System.out.println("Payment task submitted.");

            try {
                // This throws ExecutionException because the task failed.
                String result = paymentFuture.get();
                System.out.println("Payment result: " + result);

            } catch (ExecutionException e) {
                System.out.println("Payment failed.");
                System.out.println(
                        "Underlying cause: "
                                + e.getCause().getClass().getSimpleName());
                System.out.println(
                        "Reason: " + e.getCause().getMessage());
            }

            System.out.println(
                    "Main thread continues running.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(
                    "Main thread was interrupted.");
        } finally {
            executor.shutdown();
        }
    }
}
