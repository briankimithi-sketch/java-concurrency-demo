package dev.yobi.concurrency;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable order data shared with background tasks.
 */
public record Order(String id, int quantity, BigDecimal unitPrice) {
    public Order {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Order id must not be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Objects.requireNonNull(unitPrice, "Unit price must not be null");
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Unit price must not be negative");
        }
    }

    public BigDecimal total() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
