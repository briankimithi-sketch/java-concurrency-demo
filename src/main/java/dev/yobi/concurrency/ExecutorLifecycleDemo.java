package dev.yobi.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorLifecycleDemo {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            for (int i = 1; i <= 5; i++) {
                int taskId = i;

                executor.submit(() -> {
                    System.out.println(
                            "Task " + taskId + " running on "
                                    + Thread.currentThread().getName()
                    );
                });
            }
        } finally {
            executor.shutdown();
        }

        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            executor.shutdownNow();

            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate in time");
            }
        }

        System.out.println("Executor terminated: " + executor.isTerminated());
    }
}
