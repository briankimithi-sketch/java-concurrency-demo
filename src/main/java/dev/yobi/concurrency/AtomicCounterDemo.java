package dev.yobi.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounterDemo {

    private final AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }

    public static void main(String[] args)
            throws InterruptedException {

        AtomicCounterDemo counter = new AtomicCounterDemo();

        int numberOfThreads = 8;
        int incrementsPerThread = 100_000;

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        int expected = numberOfThreads * incrementsPerThread;
        int actual = counter.getCount();

        System.out.println("Expected: " + expected);
        System.out.println("Actual:   " + actual);
        System.out.println("Success:  " + (expected == actual));
    }
}
