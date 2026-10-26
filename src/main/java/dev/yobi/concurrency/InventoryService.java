package dev.yobi.concurrency;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread-safe inventory management.
 *
 * Synchronized methods ensure that checking and reserving stock
 * happen as one indivisible operation.
 */
public final class InventoryService {

    private final Map<String, Integer> stock = new HashMap<>();

    public synchronized void addProduct(String productId, int quantity) {
        validateProductId(productId);

        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity must not be negative");
        }

        if (stock.containsKey(productId)) {
            throw new IllegalArgumentException(
                    "Product already exists: " + productId);
        }

        stock.put(productId, quantity);
    }

    public synchronized boolean reserve(
            String productId, int quantity) {

        validateProductId(productId);

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Reservation quantity must be positive");
        }

        Integer available = stock.get(productId);

        if (available == null) {
            throw new IllegalArgumentException(
                    "Unknown product: " + productId);
        }

        if (available < quantity) {
            return false;
        }

        stock.put(productId, available - quantity);
        return true;
    }

    public synchronized int availableStock(String productId) {
        validateProductId(productId);

        Integer available = stock.get(productId);

        if (available == null) {
            throw new IllegalArgumentException(
                    "Unknown product: " + productId);
        }

        return available;
    }

    private static void validateProductId(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException(
                    "Product ID must not be blank");
        }
    }
}
