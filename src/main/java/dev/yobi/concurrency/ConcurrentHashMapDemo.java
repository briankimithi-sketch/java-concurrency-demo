package dev.yobi.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentHashMapDemo {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> orderCounts = new ConcurrentHashMap<>();

        int threadCount = 8;
        int updatesPerThread = 10_000;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int thread = 0; thread < threadCount; thread++) {
            executor.submit(() -> {
                for (int i = 0; i < updatesPerThread; i++) {
                    orderCounts.merge("Wireless Headphones", 1, Integer::sum);
                    orderCounts.merge("Keyboard", 1, Integer::sum);
                }
            });
        }

        executor.shutdown();

        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            throw new IllegalStateException("Tasks did not finish in time");
        }

        System.out.println("Order counts: " + orderCounts);
        System.out.println(
                "Expected count per product: " + (threadCount * updatesPerThread)
        );
    }
}
