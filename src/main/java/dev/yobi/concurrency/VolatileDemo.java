package dev.yobi.concurrency;

public class VolatileDemo {

    private static volatile boolean running = true;

    public static void main(String[] args)
            throws InterruptedException {

        Thread worker = new Thread(() -> {
            long checks = 0;

            while (running) {
                checks++;
            }

            System.out.println(
                    "Worker noticed the stop signal after "
                            + checks + " checks."
            );
        });

        worker.start();

        Thread.sleep(1000);

        System.out.println("Main thread requests shutdown.");
        running = false;

        worker.join();

        System.out.println("Worker terminated successfully.");
    }
}
