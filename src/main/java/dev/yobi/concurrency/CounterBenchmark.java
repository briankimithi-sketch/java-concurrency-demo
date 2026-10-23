package dev.yobi.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class CounterBenchmark {

    private static final int THREADS = 8;
    private static final int INCREMENTS_PER_THREAD = 500_000;
    private static final int EXPECTED =
            THREADS * INCREMENTS_PER_THREAD;

    interface Counter {
        void increment();
        int get();
    }

    // Approach 1: synchronized
    static class SynchronizedCounter implements Counter {
        private int count;

        @Override
        public synchronized void increment() {
            count++;
        }

        @Override
        public synchronized int get() {
            return count;
        }
    }

    // Approach 2: AtomicInteger
    static class AtomicCounter implements Counter {
        private final AtomicInteger count = new AtomicInteger();

        @Override
        public void increment() {
            count.incrementAndGet();
        }

        @Override
        public int get() {
            return count.get();
        }
    }

    // Approach 3: ReentrantLock
    static class LockCounter implements Counter {
        private final ReentrantLock lock = new ReentrantLock();
        private int count;

        @Override
        public void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int get() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
    }

    private static void measure(String name, Counter counter)
            throws Exception {

        ExecutorService executor =
                Executors.newFixedThreadPool(THREADS);
        CountDownLatch ready = new CountDownLatch(THREADS);
        CountDownLatch start = new CountDownLatch(1);

        try {
            Future<?>[] tasks = new Future<?>[THREADS];

            for (int i = 0; i < THREADS; i++) {
                tasks[i] = executor.submit(() -> {
                    ready.countDown();
                    start.await();

                    for (int j = 0;
                         j < INCREMENTS_PER_THREAD; j++) {
                        counter.increment();
                    }

                    return null;
                });
            }

            ready.await();
            long started = System.nanoTime();
            start.countDown();

            for (Future<?> task : tasks) {
                task.get();
            }

            long elapsed = System.nanoTime() - started;

            if (counter.get() != EXPECTED) {
                throw new IllegalStateException(
                        name + " produced " + counter.get()
                        + ", expected " + EXPECTED);
            }

            System.out.printf(
                    "%-20s count=%d time=%.2f ms%n",
                    name,
                    counter.get(),
                    elapsed / 1_000_000.0
            );
        } finally {
            executor.shutdownNow();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Threads: " + THREADS);
        System.out.println("Expected increments: " + EXPECTED);
        System.out.println();

        measure("synchronized",
                new SynchronizedCounter());

        measure("AtomicInteger",
                new AtomicCounter());

        measure("ReentrantLock",
                new LockCounter());
    }
}
