package dev.yobi.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CallerRunsPolicyDemo {

    public static void main(String[] args)
            throws InterruptedException {

        CountDownLatch workersStarted = new CountDownLatch(3);
        CountDownLatch releaseWorkers = new CountDownLatch(1);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3,
                3,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(2),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        try {
            // Occupy all three workers.
            for (int i = 1; i <= 3; i++) {
                final int taskNumber = i;

                executor.execute(() -> {
                    System.out.println(
                            "Task " + taskNumber + " running on "
                                    + Thread.currentThread().getName()
                    );

                    workersStarted.countDown();

                    try {
                        releaseWorkers.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            workersStarted.await();

            System.out.println("\nWorkers busy; queue capacity is 2.\n");

            // Tasks 4 and 5 fill the queue.
            // Tasks 6, 7 and 8 run on the submitting thread.
            for (int i = 4; i <= 8; i++) {
                final int taskNumber = i;

                executor.execute(() -> {
                    System.out.println(
                            "Task " + taskNumber + " running on "
                                    + Thread.currentThread().getName()
                    );
                });
            }

            System.out.println("\nAll submissions completed.");

        } finally {
            releaseWorkers.countDown();
            executor.shutdown();

            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }

        System.out.println("Executor terminated: "
                + executor.isTerminated());
    }
}
