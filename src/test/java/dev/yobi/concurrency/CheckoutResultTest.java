package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutResultTest {

    @Test
    void storesValidCheckoutCounts() {
        CheckoutResult result = new CheckoutResult(6, 4, 4, 0, 2);

        assertEquals(6, result.submitted());
        assertEquals(4, result.accepted());
        assertEquals(4, result.completed());
        assertEquals(0, result.failed());
        assertEquals(2, result.rejected());
        assertTrue(result.allAcceptedOrdersCompleted());
    }

    @Test
    void identifiesAcceptedOrdersThatFailed() {
        CheckoutResult result = new CheckoutResult(6, 4, 3, 1, 2);

        assertEquals(3, result.completed());
        assertEquals(1, result.failed());
        assertFalse(result.allAcceptedOrdersCompleted());
    }

    @Test
    void rejectsInconsistentSubmissionCounts() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CheckoutResult(6, 3, 3, 0, 2)
        );
    }

    @Test
    void rejectsInconsistentCompletionCounts() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CheckoutResult(6, 4, 2, 1, 2)
        );
    }

    @Test
    void rejectsNegativeCounts() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CheckoutResult(6, 4, 4, -1, 2)
        );
    }

    @Test
    void rejectsCompletedOrdersGreaterThanAcceptedOrders() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CheckoutResult(6, 4, 5, 0, 2)
        );
    }
}
