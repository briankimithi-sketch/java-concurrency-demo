package dev.yobi.concurrency;

import java.util.ArrayList;
import java.util.List;

public class UnsafeCounterDemo {

    private int count = 0;

    // No synchronized keyword!
    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args)
            throws InterruptedException {

        UnsafeCounterDemo counter = new UnsafeCounterDemo();

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
