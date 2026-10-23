package dev.yobi.concurrency;

/**
 * Immutable summary of checkout processing.
 */
public record CheckoutResult(
        int submitted,
        int accepted,
        int completed,
        int failed,
        int rejected
) {
    public CheckoutResult {
        if (submitted < 0 || accepted < 0 || completed < 0
                || failed < 0 || rejected < 0) {
            throw new IllegalArgumentException(
                    "Order counts must not be negative");
        }

        if (submitted != accepted + rejected) {
            throw new IllegalArgumentException(
                    "Submitted must equal accepted plus rejected");
        }

        if (accepted != completed + failed) {
            throw new IllegalArgumentException(
                    "Accepted must equal completed plus failed");
        }
    }

    public boolean allAcceptedOrdersCompleted() {
        return accepted == completed;
    }
}
