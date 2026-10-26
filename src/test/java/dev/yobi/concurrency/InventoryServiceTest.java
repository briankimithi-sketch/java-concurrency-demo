package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    @Test
    void reservesStockWhenEnoughIsAvailable() {
        InventoryService inventory = new InventoryService();
        inventory.addProduct("TYRE-205", 10);

        assertTrue(inventory.reserve("TYRE-205", 4));
        assertEquals(6, inventory.availableStock("TYRE-205"));
    }

    @Test
    void rejectsReservationWhenStockIsInsufficient() {
        InventoryService inventory = new InventoryService();
        inventory.addProduct("TYRE-205", 3);

        assertFalse(inventory.reserve("TYRE-205", 4));
        assertEquals(3, inventory.availableStock("TYRE-205"));
    }

    @Test
    void rejectsInvalidReservationQuantity() {
        InventoryService inventory = new InventoryService();
        inventory.addProduct("TYRE-205", 10);

        assertThrows(IllegalArgumentException.class,
                () -> inventory.reserve("TYRE-205", 0));

        assertThrows(IllegalArgumentException.class,
                () -> inventory.reserve("TYRE-205", -1));
    }

    @Test
    void rejectsDuplicateProducts() {
        InventoryService inventory = new InventoryService();
        inventory.addProduct("TYRE-205", 10);

        assertThrows(IllegalArgumentException.class,
                () -> inventory.addProduct("TYRE-205", 20));
    }

    @Test
    void concurrentReservationsNeverOversellStock()
            throws Exception {

        InventoryService inventory = new InventoryService();
        inventory.addProduct("TYRE-205", 10);

        int customers = 30;
        ExecutorService executor =
                Executors.newFixedThreadPool(10);

        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> reservations = new ArrayList<>();

        try {
            for (int i = 0; i < customers; i++) {
                reservations.add(executor.submit(() -> {
                    start.await();
                    return inventory.reserve("TYRE-205", 1);
                }));
            }

            // Release the waiting tasks after all have been submitted.
            start.countDown();

            int successfulReservations = 0;

            for (Future<Boolean> reservation : reservations) {
                if (reservation.get()) {
                    successfulReservations++;
                }
            }

            assertEquals(10, successfulReservations);
            assertEquals(0, inventory.availableStock("TYRE-205"));
        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }
}
