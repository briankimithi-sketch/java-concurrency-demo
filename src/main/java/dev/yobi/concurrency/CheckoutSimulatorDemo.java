package dev.yobi.concurrency;

public class CheckoutSimulatorDemo {

    public static void main(String[] args) throws InterruptedException {
        CheckoutService service = new CheckoutService(2, 2);
        CheckoutResult result = service.processOrders(6);

        System.out.println("========== FINAL RESULTS ==========");
        System.out.println("Orders submitted: " + result.submitted());
        System.out.println("Accepted: " + result.accepted());
        System.out.println("Completed: " + result.completed());
        System.out.println("Rejected: " + result.rejected());

        if (result.allAcceptedOrdersCompleted()) {
            System.out.println("Completion accounting: VERIFIED");
        } else {
            System.out.println("WARNING: Some accepted orders did not complete");
        }
    }
}
