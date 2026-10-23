package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsyncProductStockDemoTest {

    @Test
    void returnsStockWhenProductIsAvailable() {
        String result = AsyncProductStockDemo.checkStock(
                "Wireless Headphones").join();

        assertEquals(
                "Wireless Headphones | Stock: Available",
                result);
    }

    @Test
    void recoversWhenStockServiceFails() {
        String result = AsyncProductStockDemo.checkStock(
                "Unknown Product")
                .exceptionally(error -> "Stock status unavailable")
                .join();

        assertEquals("Stock status unavailable", result);
    }

    @Test
    void recoversWhenProductNameIsMissing() {
        String result = AsyncProductStockDemo.checkStock(null)
                .exceptionally(error -> "Stock status unavailable")
                .join();

        assertEquals("Stock status unavailable", result);
    }
}
