package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessingTaskTest {
    @Test
    void runnableProcessesOrder() throws InterruptedException {
        Order order = new Order("ORD-2", 1, new BigDecimal("99.00"));
        AtomicReference<Order> processed = new AtomicReference<>();
        CountDownLatch finished = new CountDownLatch(1);

        OrderProcessingTask task = new OrderProcessingTask(order, value -> {
            processed.set(value);
            finished.countDown();
        });

        Thread thread = new Thread(task, "order-test-thread");
        thread.start();

        assertTrue(finished.await(2, TimeUnit.SECONDS), "Runnable did not finish in time");
        assertSame(order, processed.get());
        thread.join(2_000);
        assertFalse(thread.isAlive(), "Worker thread should have finished");
    }
}
