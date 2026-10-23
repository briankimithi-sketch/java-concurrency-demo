package dev.yobi.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class CheckoutService {

    private final int workerCount;
    private final int queueCapacity;

    public CheckoutService(int workerCount, int queueCapacity) {
        if (workerCount <= 0 || queueCapacity <= 0) {
            throw new IllegalArgumentException(
                    "Worker count and queue capacity must be positive");
        }

        this.workerCount = workerCount;
        this.queueCapacity = queueCapacity;
    }

    public CheckoutResult processOrders(int totalOrders)
            throws InterruptedException {

        if (totalOrders < 0) {
            throw new IllegalArgumentException(
                    "Total orders must not be negative");
        }

        AtomicInteger accepted = new AtomicInteger();
        AtomicInteger completed = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        CountDownLatch workersStarted = new CountDownLatch(workerCount);
        CountDownLatch releaseWorkers = new CountDownLatch(1);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                workerCount,
                workerCount,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.AbortPolicy()
        );

        InterruptedException interruption = null;

        try {
            for (int orderId = 1; orderId <= totalOrders; orderId++) {
                final int id = orderId;

                try {
                    executor.execute(() -> {
                        if (id <= workerCount) {
                            workersStarted.countDown();
                        }

                        try {
                            releaseWorkers.await();
                            completed.incrementAndGet();
                        } catch (InterruptedException e) {
                            failed.incrementAndGet();
                            Thread.currentThread().interrupt();
                        } catch (RuntimeException e) {
                            failed.incrementAndGet();
                        }
                    });

                    accepted.incrementAndGet();

                } catch (RejectedExecutionException e) {
                    rejected.incrementAndGet();
                }

                // Hold initial worker tasks so the queue fills predictably.
                if (orderId == workerCount) {
                    workersStarted.await();
                }
            }
        } catch (InterruptedException e) {
            interruption = e;
        } finally {
            releaseWorkers.countDown();

            if (interruption != null) {
                executor.shutdownNow();
            } else {
                executor.shutdown();
            }

            boolean terminated = false;
            long deadline = System.nanoTime()
                    + TimeUnit.SECONDS.toNanos(15);

            while (!terminated && System.nanoTime() < deadline) {
                try {
                    terminated = executor.awaitTermination(
                            200, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    if (interruption == null) {
                        interruption = e;
                    }

                    executor.shutdownNow();
                }
            }

            if (!terminated) {
                executor.shutdownNow();
            }

            if (interruption != null) {
                Thread.currentThread().interrupt();
            }
        }

        if (interruption != null) {
            throw interruption;
        }

        if (!executor.isTerminated()) {
            throw new IllegalStateException(
                    "Checkout executor did not terminate");
        }

        return new CheckoutResult(
                totalOrders,
                accepted.get(),
                completed.get(),
                failed.get(),
                rejected.get()
        );
    }
}
