package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutServiceTest {

    @Test
    void acceptsOrdersUpToWorkerAndQueueCapacity()
            throws InterruptedException {

        CheckoutService service = new CheckoutService(2, 2);

        CheckoutResult result = service.processOrders(6);

        assertEquals(6, result.submitted());
        assertEquals(4, result.accepted());
        assertEquals(4, result.completed());
        assertEquals(0, result.failed());
        assertEquals(2, result.rejected());

        assertEquals(
                result.submitted(),
                result.accepted() + result.rejected()
        );
        assertEquals(
                result.accepted(),
                result.completed() + result.failed()
        );
        assertTrue(result.allAcceptedOrdersCompleted());
    }

    @Test
    void acceptsAllOrdersWhenCapacityIsSufficient()
            throws InterruptedException {

        CheckoutService service = new CheckoutService(2, 4);

        CheckoutResult result = service.processOrders(6);

        assertEquals(6, result.submitted());
        assertEquals(6, result.accepted());
        assertEquals(6, result.completed());
        assertEquals(0, result.failed());
        assertEquals(0, result.rejected());
        assertTrue(result.allAcceptedOrdersCompleted());
    }

    @Test
    void handlesZeroOrders()
            throws InterruptedException {

        CheckoutService service = new CheckoutService(2, 2);

        CheckoutResult result = service.processOrders(0);

        assertEquals(0, result.submitted());
        assertEquals(0, result.accepted());
        assertEquals(0, result.completed());
        assertEquals(0, result.failed());
        assertEquals(0, result.rejected());
        assertTrue(result.allAcceptedOrdersCompleted());
    }

    @Test
    void rejectsNegativeOrderCount() {

        CheckoutService service = new CheckoutService(2, 2);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.processOrders(-1)
        );
    }

    @Test
    void rejectsInvalidPoolConfiguration() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new CheckoutService(0, 2)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CheckoutService(2, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CheckoutService(-1, 2)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CheckoutService(2, -1)
        );
    }
}
