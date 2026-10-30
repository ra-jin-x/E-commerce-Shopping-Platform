package com.ecommerce.cli;

import java.util.List;
import java.util.Scanner;
import com.ecommerce.model.Refund;
import com.ecommerce.model.ReturnRequest;
import com.ecommerce.model.Review;
import com.ecommerce.services.returns.RefundService;
import com.ecommerce.services.returns.ReturnService;
import com.ecommerce.services.returns.ReviewService;


public class ReturnReviewCLI {
    private Scanner in;
    private ReturnService returnService;
    private RefundService refundService;
    private ReviewService reviewService;

    public ReturnReviewCLI(Scanner in, ReturnService returnService, RefundService refundService,
                           ReviewService reviewService) {
        this.in = in;
        this.returnService = returnService;
        this.refundService = refundService;
        this.reviewService = reviewService;
    }

    // ---------------- Customer ----------------
    public void customerMenu(String customerId) {
        while (true) {
            System.out.println("\n--- Returns, Refunds and Reviews (Customer " + customerId + ") ---");
            System.out.println("1. Request a return");
            System.out.println("2. My return requests");
            System.out.println("3. Refund for a cancelled order");
            System.out.println("4. My refunds");
            System.out.println("5. Add a review");
            System.out.println("6. View reviews of a product");
            System.out.println("0. Back");
            String choice = ask("Choose: ");
            try {
                if (choice.equals("0")) return;
                else if (choice.equals("1")) requestReturn(customerId);
                else if (choice.equals("2")) printReturns(returnService.getReturnsByCustomer(customerId));
                else if (choice.equals("3")) cancelledOrderRefund(customerId);
                else if (choice.equals("4")) printRefunds(refundService.getRefundsByCustomer(customerId));
                else if (choice.equals("5")) addReview(customerId);
                else if (choice.equals("6")) viewReviews();
                else System.out.println("   Invalid choice");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("   [NOT ALLOWED] " + e.getMessage());
            }
        }
    }

    // ---------------- Seller ----------------
    public void sellerMenu() {
        staffMenu("Seller", false);
    }

    // ---------------- Admin ----------------
    public void adminMenu() {
        staffMenu("Admin", true);
    }

    private void staffMenu(String role, boolean isAdmin) {
        while (true) {
            System.out.println("\n--- Returns, Refunds and Reviews (" + role + ") ---");
            System.out.println("1. View pending returns");
            System.out.println("2. Approve a return");
            System.out.println("3. Reject a return");
            System.out.println("4. View all refunds");
            System.out.println("5. View reviews of a product");
            if (isAdmin) System.out.println("6. View products flagged for review");
            System.out.println("0. Back");
            String choice = ask("Choose: ");
            try {
                if (choice.equals("0")) return;
                else if (choice.equals("1")) printReturns(returnService.getPendingReturns());
                else if (choice.equals("2")) approve(role);
                else if (choice.equals("3")) reject(role);
                else if (choice.equals("4")) printRefunds(refundService.getAllRefunds());
                else if (choice.equals("5")) viewReviews();
                else if (choice.equals("6") && isAdmin) viewFlagged();
                else System.out.println("   Invalid choice");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("   [NOT ALLOWED] " + e.getMessage());
            }
        }
    }

    // ---------------- actions ----------------
    private void requestReturn(String customerId) {
        System.out.println("(Order details come from the Order module; type them here for now.)");
        String orderId = ask("Order id: ");
        String productId = ask("Product id: ");
        String productName = ask("Product name: ");
        String category = ask("Category (Electronics/Fashion/Home/Books/Grocery): ");
        String method = ask("Payment method (Card/UPI/Net Banking/COD): ");
        int quantity = askInt("Quantity: ");
        double amount = askDouble("Item amount (price x quantity, without delivery charge): ");
        int days = askInt("Days since delivery: ");
        String reason = ask("Reason: ");
        ReturnRequest r = returnService.requestReturn(orderId, productId, productName, category,
                customerId, method, quantity, amount, days, reason);
        System.out.println("   Return " + r.getId() + " created. Status: " + r.getStatus());
    }

    private void cancelledOrderRefund(String customerId) {
        String orderId = ask("Order id: ");
        String status = ask("Order status (Placed/Packed/Shipped...): ");
        String method = ask("Payment method (Card/UPI/Net Banking/COD): ");
        double total = askDouble("Order total paid: ");
        Refund refund = refundService.refundForCancelledOrder(orderId, customerId, status, total, method);
        System.out.printf("   Refund %s: Rs. %.2f given back to your original %s (full amount).%n",
                refund.getId(), refund.getAmount(), refund.getPaymentMethod());
    }

    private void approve(String role) {
        String returnId = ask("Return id to approve: ");
        Refund refund = returnService.approveReturn(returnId, role);
        ReturnRequest r = returnService.getReturn(returnId);
        System.out.printf("   Approved. Refund %s: Rs. %.2f given back to original %s (delivery charge not refunded).%n",
                refund.getId(), refund.getAmount(), refund.getPaymentMethod());
        System.out.println("   Add " + r.getQuantity() + " unit(s) of " + r.getProductName() + " back to stock.");
    }

    private void reject(String role) {
        ReturnRequest r = returnService.rejectReturn(ask("Return id to reject: "), role);
        System.out.println("   Return " + r.getId() + " rejected.");
    }

    private void addReview(String customerId) {
        String orderId = ask("Order id: ");
        String productId = ask("Product id: ");
        boolean delivered = ask("Is the order Delivered? (yes/no): ").equalsIgnoreCase("yes");
        int rating = askInt("Rating (1-5): ");
        String comment = ask("Comment: ");
        Review r = reviewService.addReview(orderId, productId, customerId, delivered, rating, comment);
        System.out.printf("   Review %s saved. New average for %s: %.2f%n",
                r.getId(), productId, reviewService.getAverage(productId));
    }

    private void viewReviews() {
        String productId = ask("Product id: ");
        List<Review> list = reviewService.getReviews(productId);
        if (list.isEmpty()) {
            System.out.println("   No reviews yet.");
            return;
        }
        System.out.printf("   %s: %d review(s), average %.2f%n", productId, list.size(), reviewService.getAverage(productId));
        for (Review r : list) {
            System.out.printf("   %s | %s | %d star | %s%n", r.getId(), r.getCustomerId(), r.getRating(), r.getComment());
        }
    }

    private void viewFlagged() {
        List<String> flagged = reviewService.getFlaggedProducts();
        if (flagged.isEmpty()) {
            System.out.println("   (no flagged products)");
            return;
        }
        for (String productId : flagged) {
            System.out.printf("   %s | %d reviews | average %.2f%n", productId,
                    reviewService.getReviews(productId).size(), reviewService.getAverage(productId));
        }
    }

    private void printReturns(List<ReturnRequest> list) {
        if (list.isEmpty()) {
            System.out.println("   (none)");
            return;
        }
        for (ReturnRequest r : list) {
            System.out.printf("   %s | order %s | %s x%d | Rs. %.2f | %s | %s%n", r.getId(), r.getOrderId(),
                    r.getProductName(), r.getQuantity(), r.getAmount(), r.getStatus(), r.getReason());
        }
    }

    private void printRefunds(List<Refund> list) {
        if (list.isEmpty()) {
            System.out.println("   (no refunds)");
            return;
        }
        for (Refund r : list) {
            System.out.printf("   %s | order %s | Rs. %.2f | %s | %s | %s%n", r.getId(), r.getOrderId(),
                    r.getAmount(), r.getPaymentMethod(), r.getDate(), r.getReason());
        }
    }

    // ---------------- input helpers ----------------
    private String ask(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }

    private int askInt(String prompt) {
        try {
            return Integer.parseInt(ask(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a whole number");
        }
    }

    private double askDouble(String prompt) {
        try {
            return Double.parseDouble(ask(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a number");
        }
    }
}
