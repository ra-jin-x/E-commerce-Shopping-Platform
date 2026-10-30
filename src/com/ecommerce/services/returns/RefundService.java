package com.ecommerce.services.returns;

import java.time.LocalDate;
import java.util.List;
import com.ecommerce.model.Refund;
import com.ecommerce.model.ReturnRequest;
import com.ecommerce.repository.RefundRepository;

/** Refund rules (Module 5). */
public class RefundService {
    private RefundRepository refundRepo;

    public RefundService(RefundRepository refundRepo) {
        this.refundRepo = refundRepo;
    }

    /** Accepts Card, UPI, Net Banking or COD (any case) and returns the standard name. */
    public static String normalizeMethod(String method) {
        String m = method.trim().toLowerCase();
        if (m.equals("card")) return "Card";
        if (m.equals("upi")) return "UPI";
        if (m.equals("net banking") || m.equals("netbanking")) return "Net Banking";
        if (m.equals("cod")) return "COD";
        throw new IllegalArgumentException("Payment method must be Card, UPI, Net Banking or COD");
    }

    /** Refund for an approved return: item amount only, to the original payment method. */
    public Refund refundForReturn(ReturnRequest r) {
        Refund refund = new Refund(refundRepo.nextId(), r.getOrderId(), r.getCustomerId(), r.getAmount(),
                r.getPaymentMethod(), LocalDate.now(), "Return of " + r.getProductName());
        refundRepo.save(refund);
        return refund;
    }

    /**
     * Refund for an order cancelled before it was Shipped.
     * Prepaid orders get the full amount back (including the delivery charge).
     * COD orders were never paid, so there is nothing to refund.
     */
    public Refund refundForCancelledOrder(String orderId, String customerId, String orderStatus,
                                          double totalAmount, String paymentMethod) {
        String status = orderStatus.trim().toLowerCase();
        if (!status.equals("placed") && !status.equals("packed")) {
            throw new IllegalStateException("An order can be cancelled only before it is Shipped");
        }
        String method = normalizeMethod(paymentMethod);
        if (method.equals("COD")) {
            throw new IllegalStateException("COD order was not paid, so there is nothing to refund");
        }
        if (refundRepo.cancellationRefundExists(orderId)) {
            throw new IllegalStateException("This order is already refunded");
        }
        Refund refund = new Refund(refundRepo.nextId(), orderId, customerId, totalAmount,
                method, LocalDate.now(), "Cancelled before shipping");
        refundRepo.save(refund);
        return refund;
    }

    public List<Refund> getAllRefunds() {
        return refundRepo.findAll();
    }

    public List<Refund> getRefundsByCustomer(String customerId) {
        return refundRepo.findByCustomer(customerId);
    }
}
