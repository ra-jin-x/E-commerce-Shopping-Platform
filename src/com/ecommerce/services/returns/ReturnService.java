package com.ecommerce.services.returns;

import java.util.ArrayList;
import java.util.List;
import com.ecommerce.model.Refund;
import com.ecommerce.model.ReturnRequest;
import com.ecommerce.repository.ReturnRepository;

/** Return rules (Module 5). Refund money is handled by RefundService. */
public class ReturnService {
    private static final int RETURN_DAYS = 7;

    private ReturnRepository returnRepo;
    private RefundService refundService;

    public ReturnService(ReturnRepository returnRepo, RefundService refundService) {
        this.returnRepo = returnRepo;
        this.refundService = refundService;
    }

    /** Customer asks to return a product within 7 days of delivery. Grocery cannot be returned. */
    public ReturnRequest requestReturn(String orderId, String productId, String productName, String category,
                                       String customerId, String paymentMethod, int quantity, double amount,
                                       int daysSinceDelivery, String reason) {
        if (quantity <= 0 || amount <= 0) {
            throw new IllegalArgumentException("Quantity and amount must be greater than zero");
        }
        if (category.trim().equalsIgnoreCase("Grocery")) {
            throw new IllegalStateException("Grocery products cannot be returned");
        }
        if (daysSinceDelivery < 0 || daysSinceDelivery > RETURN_DAYS) {
            throw new IllegalStateException("Returns are allowed only within " + RETURN_DAYS + " days of delivery");
        }
        if (returnRepo.exists(orderId, productId)) {
            throw new IllegalStateException("A return already exists for this product in this order");
        }
        String method = RefundService.normalizeMethod(paymentMethod);
        ReturnRequest r = new ReturnRequest(returnRepo.nextId(), orderId, productId, productName, category,
                customerId, method, quantity, amount, reason);
        returnRepo.save(r);
        return r;
    }

    /**
     * Seller or Admin approves the return: full item refund to the original payment method
     * (delivery charge not refunded). The quantity must be added back to product stock.
     */
    public Refund approveReturn(String returnId, String role) {
        checkStaff(role);
        ReturnRequest r = findPending(returnId);
        Refund refund = refundService.refundForReturn(r);
        r.approve();
        return refund;
    }

    public ReturnRequest rejectReturn(String returnId, String role) {
        checkStaff(role);
        ReturnRequest r = findPending(returnId);
        r.reject();
        return r;
    }

    public ReturnRequest getReturn(String returnId) {
        return returnRepo.findById(returnId);
    }

    public List<ReturnRequest> getPendingReturns() {
        List<ReturnRequest> result = new ArrayList<>();
        for (ReturnRequest r : returnRepo.findAll()) {
            if (r.getStatus().equals("Requested")) result.add(r);
        }
        return result;
    }

    public List<ReturnRequest> getReturnsByCustomer(String customerId) {
        List<ReturnRequest> result = new ArrayList<>();
        for (ReturnRequest r : returnRepo.findAll()) {
            if (r.getCustomerId().equalsIgnoreCase(customerId)) result.add(r);
        }
        return result;
    }

    private ReturnRequest findPending(String returnId) {
        ReturnRequest r = returnRepo.findById(returnId);
        if (r == null) throw new IllegalArgumentException("Return not found: " + returnId);
        if (!r.getStatus().equals("Requested")) {
            throw new IllegalStateException("This return is already " + r.getStatus());
        }
        return r;
    }

    private void checkStaff(String role) {
        if (!role.equals("Seller") && !role.equals("Admin")) {
            throw new IllegalStateException("Only a Seller or Admin can approve or reject returns");
        }
    }
}
