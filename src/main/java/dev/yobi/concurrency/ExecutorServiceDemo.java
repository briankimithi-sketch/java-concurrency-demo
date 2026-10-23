package dev.yobi.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceDemo {

    public static void main(String[] args)
            throws Exception {

        ExecutorService executor =
                Executors.newFixedThreadPool(4);

        try {
            List<Future<Integer>> results = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                final int taskId = i;

                Callable<Integer> task = () -> {
                    String threadName =
                            Thread.currentThread().getName();

                    System.out.println(
                            "Task " + taskId
                            + " running on " + threadName);

                    return taskId * taskId;
                };

                results.add(executor.submit(task));
            }

            int total = 0;

            for (Future<Integer> result : results) {
                total += result.get();
            }

            System.out.println("Total of squares: " + total);

        } finally {
            executor.shutdown();
        }
    }
}
