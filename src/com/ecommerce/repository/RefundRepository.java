package com.ecommerce.repository;

import java.util.ArrayList;
import java.util.List;
import com.ecommerce.model.Refund;

/** In-memory storage for refunds. */
public class RefundRepository {
    private List<Refund> refunds = new ArrayList<>();
    private int counter = 1;

    public String nextId() {
        return "RF" + counter++;
    }

    public void save(Refund r) {
        refunds.add(r);
    }

    public List<Refund> findAll() {
        return refunds;
    }

    public List<Refund> findByCustomer(String customerId) {
        List<Refund> result = new ArrayList<>();
        for (Refund r : refunds) {
            if (r.getCustomerId().equalsIgnoreCase(customerId)) result.add(r);
        }
        return result;
    }

    /** True if this order was already refunded because it was cancelled. */
    public boolean cancellationRefundExists(String orderId) {
        for (Refund r : refunds) {
            if (r.getOrderId().equalsIgnoreCase(orderId) && r.getReason().startsWith("Cancelled")) return true;
        }
        return false;
    }
}
