package dev.yobi.concurrency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureExceptionDemo {

    public static void main(String[] args) {
        ExecutorService executor =
                Executors.newFixedThreadPool(3);

        try {
            Future<Integer> task1 =
                    executor.submit(() -> {
                        System.out.println("Task 1 executing");
                        return 100;
                    });

            Future<Integer> task2 =
                    executor.submit(() -> {
                        System.out.println("Task 2 executing");
                        throw new IllegalStateException(
                                "Something went wrong in task 2");
                    });

            Future<Integer> task3 =
                    executor.submit(() -> {
                        System.out.println("Task 3 executing");
                        return 300;
                    });

            readResult("Task 1", task1);
            readResult("Task 2", task2);
            readResult("Task 3", task3);

        } finally {
            executor.shutdown();
        }
    }

    private static void readResult(
            String taskName, Future<Integer> future) {

        try {
            Integer result = future.get();
            System.out.println(taskName + " result: " + result);

        } catch (ExecutionException e) {
            System.out.println(taskName + " failed: "
                    + e.getCause().getMessage());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(taskName + " was interrupted.");
        }
    }
}
