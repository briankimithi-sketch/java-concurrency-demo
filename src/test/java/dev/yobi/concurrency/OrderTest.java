package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    @Test
    void calculatesOrderTotal() {
        Order order = new Order("ORD-1", 3, new BigDecimal("12.50"));
        assertEquals(new BigDecimal("37.50"), order.total());
    }

    @Test
    void rejectsZeroQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order("ORD-1", 0, new BigDecimal("12.50")));
    }

    @Test
    void rejectsNegativeUnitPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order("ORD-1", 1, new BigDecimal("-1.00")));
    }
}
