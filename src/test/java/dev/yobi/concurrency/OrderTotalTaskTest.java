package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderTotalTaskTest {
    @Test
    void callableReturnsTotalThroughFuture() throws Exception {
        Order order = new Order("ORD-3", 4, new BigDecimal("250.25"));
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<BigDecimal> future = executor.submit(new OrderTotalTask(order));
            assertEquals(new BigDecimal("1001.00"), future.get(2, TimeUnit.SECONDS));
            assertTrue(future.isDone());
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
        }
    }

    @Test
    void futureExposesTaskExceptions() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<Integer> future = executor.submit((Callable<Integer>) () -> {
                throw new IllegalStateException("simulated task failure");
            });

            ExecutionException exception = assertThrows(
                    ExecutionException.class,
                    () -> future.get(2, TimeUnit.SECONDS)
            );
            assertInstanceOf(IllegalStateException.class, exception.getCause());
            assertEquals("simulated task failure", exception.getCause().getMessage());
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
        }
    }
}
