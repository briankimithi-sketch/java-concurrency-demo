package dev.yobi.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BoundedThreadPoolDemo {

    public static void main(String[] args)
            throws InterruptedException {

        int poolSize = 3;
        int queueCapacity = 2;

        CountDownLatch workersStarted = new CountDownLatch(poolSize);
        CountDownLatch releaseWorkers = new CountDownLatch(1);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.AbortPolicy()
        );

        int accepted = 0;
        int rejected = 0;

        try {
            // Fill all three worker threads.
            for (int i = 1; i <= poolSize; i++) {
                final int taskNumber = i;

                executor.execute(() -> {
                    System.out.println(
                            "Task " + taskNumber
                                    + " started on "
                                    + Thread.currentThread().getName()
                    );

                    workersStarted.countDown();

                    try {
                        releaseWorkers.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("Task " + taskNumber + " finished");
                });

                accepted++;
            }

            // Ensure all three workers are occupied before filling the queue.
            workersStarted.await();

            System.out.println("\nAll workers are busy.");

            // Try submitting five more tasks.
            for (int i = 4; i <= 8; i++) {
                final int taskNumber = i;

                try {
                    executor.execute(() -> {
                        System.out.println(
                                "Task " + taskNumber
                                        + " started on "
                                        + Thread.currentThread().getName()
                        );

                        try {
                            releaseWorkers.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        System.out.println(
                                "Task " + taskNumber + " finished");
                    });

                    accepted++;
                    System.out.println("Task " + taskNumber + " accepted");
                } catch (RejectedExecutionException e) {
                    rejected++;
                    System.out.println(
                            "Task " + taskNumber + " REJECTED: "
                                    + "workers busy and queue full"
                    );
                }
            }

            System.out.println("\n========== EXECUTOR STATUS ==========");
            System.out.println("Active workers: " + executor.getActiveCount());
            System.out.println("Queued tasks: " + executor.getQueue().size());
            System.out.println("Accepted tasks: " + accepted);
            System.out.println("Rejected tasks: " + rejected);

        } finally {
            // Let running and queued tasks finish.
            releaseWorkers.countDown();

            executor.shutdown();

            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                System.out.println("Executor forced to stop.");
            }
        }

        System.out.println("\nExecutor terminated: "
                + executor.isTerminated());
    }
}
