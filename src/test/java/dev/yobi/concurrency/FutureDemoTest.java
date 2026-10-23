package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FutureDemoTest {

    @Test
    void shouldReturnResultsFromConcurrentTasks() throws Exception {
        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        try {
            Callable<Double> subtotalTask =
                    () -> 4500.00 * 2;

            Callable<Double> taxTask =
                    () -> 4500.00 * 2 * 0.16;

            Future<Double> subtotalFuture =
                    executor.submit(subtotalTask);

            Future<Double> taxFuture =
                    executor.submit(taxTask);

            assertEquals(9000.00, subtotalFuture.get(), 0.001);
            assertEquals(1440.00, taxFuture.get(), 0.001);
        } finally {
            executor.shutdown();
        }
    }
}
