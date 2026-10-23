package dev.yobi.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureDemo {

    public static void main(String[] args) throws Exception {

        // 1. Create a pool containing two worker threads.
        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        try {
            double price = 4500.00;
            int quantity = 2;

            // 2. Create a task that returns the subtotal.
            Callable<Double> subtotalTask = () -> {
                System.out.println(
                        "Calculating subtotal on "
                                + Thread.currentThread().getName());

                return price * quantity;
            };

            // 3. Create a task that calculates tax.
            Callable<Double> taxTask = () -> {
                System.out.println(
                        "Calculating tax on "
                                + Thread.currentThread().getName());

                return price * quantity * 0.16;
            };

            // 4. Submit both tasks to the thread pool.
            Future<Double> subtotalFuture =
                    executor.submit(subtotalTask);

            Future<Double> taxFuture =
                    executor.submit(taxTask);

            System.out.println("Tasks submitted.");

            // 5. Collect their results.
            // get() waits if a result is not ready yet.
            double subtotal = subtotalFuture.get();
            double tax = taxFuture.get();

            // 6. Display the final order summary.
            System.out.printf("Subtotal: KSh %.2f%n", subtotal);
            System.out.printf("Tax:      KSh %.2f%n", tax);
            System.out.printf(
                    "Total:    KSh %.2f%n",
                    subtotal + tax);

        } finally {
            // 7. Shut down the executor when finished.
            executor.shutdown();
        }
    }
}
