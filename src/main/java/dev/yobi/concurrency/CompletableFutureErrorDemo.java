package dev.yobi.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class CompletableFutureErrorDemo {

    public static void main(String[] args) {

        System.out.println("=== exceptionally() ===");

        CompletableFuture<String> paymentFuture =
                CompletableFuture.supplyAsync(() -> {
                    throw new IllegalStateException(
                            "Payment provider is unavailable"
                    );
                });

        CompletableFuture<String> recoveredPayment =
                paymentFuture.exceptionally(error -> {
                    System.out.println(
                            "Payment operation failed: "
                                    + rootCause(error).getMessage()
                    );

                    return "PAYMENT_STATUS_UNKNOWN";
                });

        System.out.println("Recovery result: " + recoveredPayment.join());

        System.out.println("\n=== handle() ===");

        CompletableFuture<String> handledPayment =
                CompletableFuture.<String>supplyAsync(() -> {
                    throw new IllegalStateException(
                            "Payment provider is unavailable"
                    );
                }).handle((result, error) -> {
                    if (error != null) {
                        System.out.println(
                                "Handling failure: "
                                        + rootCause(error).getMessage()
                        );

                        return "MANUAL_REVIEW_REQUIRED";
                    }

                    return result;
                });

        System.out.println("Handled result: " + handledPayment.join());
    }

    private static Throwable rootCause(Throwable error) {
        while (error instanceof CompletionException
                && error.getCause() != null) {
            error = error.getCause();
        }
        return error;
    }
}
