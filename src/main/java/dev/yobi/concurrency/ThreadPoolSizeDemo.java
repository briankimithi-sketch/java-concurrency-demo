package dev.yobi.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolSizeDemo {

    private static void runTasks(int poolSize) {
        ExecutorService executor =
                Executors.newFixedThreadPool(poolSize);

        try {
            long start = System.nanoTime();

            CountDownLatch done = new CountDownLatch(3);

            for (int i = 1; i <= 3; i++) {
                int taskNumber = i;

                executor.submit(() -> {
                    try {
                        System.out.println(
                                "Task " + taskNumber
                                + " on "
                                + Thread.currentThread().getName()
                        );

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }

            done.await();

            long elapsed = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - start);

            System.out.println("Pool size: " + poolSize);
            System.out.println("Elapsed: " + elapsed + " ms\n");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }

    public static void main(String[] args) {
        runTasks(1);
        runTasks(3);
    }
}
