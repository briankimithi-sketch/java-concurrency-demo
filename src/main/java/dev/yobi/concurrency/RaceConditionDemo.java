package dev.yobi.concurrency;

import java.util.concurrent.CountDownLatch;

public class RaceConditionDemo {

    private static final int THREAD_COUNT = 8;
    private static final int INCREMENTS_PER_THREAD = 1_000_000;

    static class UnsafeCounter {
        private int count = 0;

        public synchronized void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        UnsafeCounter counter = new UnsafeCounter();
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        Thread[] threads = new Thread[THREAD_COUNT];

        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(() -> {
                ready.countDown();

                try {
                    start.await();

                    for (int j = 0;
                         j < INCREMENTS_PER_THREAD; j++) {
                        counter.increment();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            threads[i].start();
        }

        ready.await();
        start.countDown();

        for (Thread thread : threads) {
            thread.join();
        }

        int expected = THREAD_COUNT * INCREMENTS_PER_THREAD;
        int actual = counter.getCount();

        System.out.println("Expected count: " + expected);
        System.out.println("Actual count:   " + actual);
        System.out.println("Lost updates:   " + (expected - actual));
    }
}
