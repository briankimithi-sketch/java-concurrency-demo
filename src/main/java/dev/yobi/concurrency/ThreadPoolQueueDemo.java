package dev.yobi.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolQueueDemo {

    public static void main(String[] args)
            throws InterruptedException {

        int taskCount = 10;
        int poolSize = 3;

        AtomicInteger completed = new AtomicInteger();

        ExecutorService executor =
                Executors.newFixedThreadPool(poolSize);

        long start = System.nanoTime();

        try {
            for (int i = 1; i <= taskCount; i++) {
                int taskNumber = i;

                executor.submit(() -> {
                    try {
                        System.out.println(
                                "Task " + taskNumber
                                + " started on "
                                + Thread.currentThread().getName()
                        );

                        Thread.sleep(1000);

                        int total = completed.incrementAndGet();

                        System.out.println(
                                "Task " + taskNumber
                                + " completed. Total completed: "
                                + total
                        );
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        } finally {
            executor.shutdown();
        }

        boolean finished =
                executor.awaitTermination(15, TimeUnit.SECONDS);

        long elapsed = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - start);

        System.out.println("\nAll tasks finished: " + finished);
        System.out.println("Tasks completed: " + completed.get());
        System.out.println("Elapsed: " + elapsed + " ms");
    }
}
