package dev.yobi.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LostUpdateDemo {

    public static void main(String[] args) throws InterruptedException {
        int workers = 2;

        int[] unsafeCounter = {0};
        CountDownLatch ready = new CountDownLatch(workers);
        CountDownLatch start = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(workers);

        try {
            for (int i = 0; i < workers; i++) {
                executor.submit(() -> {
                    ready.countDown();

                    try {
                        start.await();

                        // Both threads may read the same old value.
                        int oldValue = unsafeCounter[0];

                        // Encourage the threads to overlap.
                        Thread.yield();

                        unsafeCounter[0] = oldValue + 1;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            ready.await();
            start.countDown();
        } finally {
            executor.shutdown();
        }

        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            throw new IllegalStateException("Workers did not finish");
        }

        AtomicInteger safeCounter = new AtomicInteger();

        CountDownLatch safeStart = new CountDownLatch(1);
        ExecutorService safeExecutor = Executors.newFixedThreadPool(workers);

        try {
            for (int i = 0; i < workers; i++) {
                safeExecutor.submit(() -> {
                    try {
                        safeStart.await();
                        safeCounter.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            safeStart.countDown();
        } finally {
            safeExecutor.shutdown();
        }

        if (!safeExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
            safeExecutor.shutdownNow();
            throw new IllegalStateException("Safe workers did not finish");
        }

        System.out.println("Unsafe counter: " + unsafeCounter[0]);
        System.out.println("Atomic counter: " + safeCounter.get());
        System.out.println("Expected value: " + workers);
    }
}
