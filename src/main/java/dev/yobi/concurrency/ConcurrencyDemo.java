package dev.yobi.concurrency;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConcurrencyDemo {

    private static String simulateTask(String name)
            throws InterruptedException {

        System.out.printf("%s started on %s%n",
                name, Thread.currentThread().getName());

        Thread.sleep(500); // Simulate background work

        return name + " completed on "
                + Thread.currentThread().getName();
    }

    public static void main(String[] args) {

        // Create a pool containing three worker threads.
        ExecutorService executor =
                Executors.newFixedThreadPool(3);

        try {
            // Submit two order-total calculations.
            Future<Double> order1 = executor.submit(() -> {
                Thread.sleep(300);
                return 4500.0;
            });

            Future<Double> order2 = executor.submit(() -> {
                Thread.sleep(300);
                return 5100.0;
            });

            // Retrieve and print the order totals.
            System.out.println("Order 1 total: KSh "
                    + order1.get());

            System.out.println("Order 2 total: KSh "
                    + order2.get());

            // Demonstrate three concurrent tasks.
            System.out.println(
                    "\n--- Concurrent task demonstration ---");

            List<Callable<String>> tasks = List.of(
                    () -> simulateTask("Task A"),
                    () -> simulateTask("Task B"),
                    () -> simulateTask("Task C")
            );

            // Start measuring the concurrent batch.
            long start = System.nanoTime();

            // Submit all tasks and wait for their completion.
            List<Future<String>> results =
                    executor.invokeAll(tasks);

            // Retrieve and print each task's result.
            for (Future<String> result : results) {
                System.out.println(result.get());
            }

            // Calculate elapsed time in milliseconds.
            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - start
            );

            System.out.println("Concurrent batch time: "
                    + elapsedMs + " ms");

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.err.println(
                    "Execution interrupted: " + e.getMessage());

        } catch (ExecutionException e) {

            System.err.println(
                    "A task failed: " + e.getCause());

        } finally {

            // Initiate an orderly shutdown of the executor.
            executor.shutdown();
        }
    }
}
