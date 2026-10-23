package dev.yobi.concurrency;

public class VolatileVisibilityDemo {

    private static volatile boolean running = true;

    public static void main(String[] args)
            throws InterruptedException {

        Thread worker = new Thread(() -> {
            long iterations = 0;

            while (running) {
                iterations++;
            }

            System.out.println("Worker stopped.");
            System.out.println("Iterations: " + iterations);
        });

        worker.start();

        Thread.sleep(1000);

        System.out.println("Main thread requests stop.");
        running = false;

        worker.join();

        System.out.println("Main thread finished.");
    }
}
