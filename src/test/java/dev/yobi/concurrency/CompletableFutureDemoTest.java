package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompletableFutureDemoTest {

    @Test
    void shouldCombineSubtotalAndTax() throws Exception {
        CompletableFuture<Double> subtotalFuture =
                CompletableFuture.supplyAsync(() -> 9000.00);

        CompletableFuture<Double> taxFuture =
                CompletableFuture.supplyAsync(() -> 1440.00);

        CompletableFuture<Double> totalFuture =
                subtotalFuture.thenCombine(
                        taxFuture,
                        (subtotal, tax) -> subtotal + tax
                );

        double total = totalFuture.get(2, TimeUnit.SECONDS);

        assertEquals(10440.00, total, 0.001);
    }
}
