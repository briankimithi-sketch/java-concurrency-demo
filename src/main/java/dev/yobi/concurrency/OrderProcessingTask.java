package dev.yobi.concurrency;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Runnable task: performs an action but does not return a result.
 */
public final class OrderProcessingTask implements Runnable {
    private final Order order;
    private final Consumer<Order> processor;

    public OrderProcessingTask(Order order, Consumer<Order> processor) {
        this.order = Objects.requireNonNull(order, "Order must not be null");
        this.processor = Objects.requireNonNull(processor, "Processor must not be null");
    }

    @Override
    public void run() {
        processor.accept(order);
    }
}
