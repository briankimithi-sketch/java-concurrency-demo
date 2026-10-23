package dev.yobi.concurrency;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckoutSimulatorDemoTest {

    @Test
    void acceptsFourOrdersRejectsTwoAndCompletesAcceptedOrders()
            throws Exception {

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream capturedOut =
                     new PrintStream(output, true, StandardCharsets.UTF_8)) {

            System.setOut(capturedOut);
            CheckoutSimulatorDemo.main(new String[0]);

        } finally {
            System.setOut(originalOut);
        }

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Orders submitted: 6"), result);
        assertTrue(result.contains("Accepted: 4"), result);
        assertTrue(result.contains("Completed: 4"), result);
        assertTrue(result.contains("Rejected: 2"), result);
        assertTrue(
                result.contains("Completion accounting: VERIFIED"),
                result
        );
    }
}
