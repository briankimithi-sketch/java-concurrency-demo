package dev.yobi.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureTimeoutDemo {

    public static void main(String[] args) throws Exception {
        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        CountDownLatch started = new CountDownLatch(1);

        try {
            Future<String> future = executor.submit(() -> {
                System.out.println("Payment task started.");
                started.countDown();

                try {
                    // Simulate a slow external request.
                    Thread.sleep(10_000);
                    return "Payment completed";
                } catch (InterruptedException e) {
                    System.out.println(
                            "Payment task received interruption.");

                    // Preserve the interrupt status.
                    Thread.currentThread().interrupt();
                    throw e;
                }
            });

            started.await();

            try {
                String result =
                        future.get(1, TimeUnit.SECONDS);

                System.out.println("Result: " + result);

            } catch (TimeoutException e) {
                System.out.println(
                        "Timed out waiting for payment.");

                boolean cancelled = future.cancel(true);
                System.out.println(
                        "Cancellation requested: " + cancelled);
            }

        } finally {
            executor.shutdownNow();
        }
    }
}
