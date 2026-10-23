package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BoundedThreadPoolDemoTest {

    @Test
    void abortPolicyRejectsTaskWhenWorkerAndQueueAreFull()
            throws InterruptedException {

        CountDownLatch taskStarted = new CountDownLatch(1);
        CountDownLatch releaseTask = new CountDownLatch(1);
        AtomicInteger completedTasks = new AtomicInteger();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1),
                new ThreadPoolExecutor.AbortPolicy()
        );

        try {
            // Occupy the only worker.
            executor.execute(() -> {
                taskStarted.countDown();

                try {
                    releaseTask.await();
                    completedTasks.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            assertTrue(taskStarted.await(2, TimeUnit.SECONDS));

            // Fill the single queue slot.
            executor.execute(completedTasks::incrementAndGet);

            // Worker and queue are now full: rejection is expected.
            assertThrows(
                    RejectedExecutionException.class,
                    () -> executor.execute(
                            completedTasks::incrementAndGet)
            );

            assertEquals(1, executor.getActiveCount());
            assertEquals(1, executor.getQueue().size());

        } finally {
            releaseTask.countDown();
            executor.shutdown();

            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }

        assertTrue(executor.isTerminated());
        assertEquals(2, completedTasks.get());
    }


    @Test
    void callerRunsPolicyExecutesRejectedTaskOnSubmittingThread()
            throws InterruptedException {

        CountDownLatch taskStarted = new CountDownLatch(1);
        CountDownLatch releaseTask = new CountDownLatch(1);
        AtomicInteger completedTasks = new AtomicInteger();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        String submittingThread = Thread.currentThread().getName();
        AtomicInteger callerRunsCount = new AtomicInteger();

        try {
            // Occupy the only worker.
            executor.execute(() -> {
                taskStarted.countDown();

                try {
                    releaseTask.await();
                    completedTasks.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            assertTrue(taskStarted.await(2, TimeUnit.SECONDS));

            // Fill the queue.
            executor.execute(completedTasks::incrementAndGet);

            // This task should run immediately on the submitting thread.
            executor.execute(() -> {
                assertEquals(
                        submittingThread,
                        Thread.currentThread().getName()
                );

                callerRunsCount.incrementAndGet();
                completedTasks.incrementAndGet();
            });

            assertEquals(1, callerRunsCount.get());
            assertEquals(1, executor.getQueue().size());

        } finally {
            releaseTask.countDown();
            executor.shutdown();

            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }

        assertTrue(executor.isTerminated());
        assertEquals(3, completedTasks.get());
    }

}
