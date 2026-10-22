package dev.yobi.concurrency;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Callable task: calculates an order total and returns the result.
 */
public final class OrderTotalTask implements Callable<BigDecimal> {
    private final Order order;

    public OrderTotalTask(Order order) {
        this.order = Objects.requireNonNull(order, "Order must not be null");
    }

    @Override
    public BigDecimal call() {
        return order.total();
    }
}
