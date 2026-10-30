package com.ecommerce.repository;

import java.util.ArrayList;
import java.util.List;
import com.ecommerce.model.ReturnRequest;

/** In-memory storage for return requests. */
public class ReturnRepository {
    private List<ReturnRequest> returns = new ArrayList<>();
    private int counter = 1;

    public String nextId() {
        return "RT" + counter++;
    }

    public void save(ReturnRequest r) {  
        returns.add(r);
    }

    public ReturnRequest findById(String id) {
        for (ReturnRequest r : returns) {
            if (r.getId().equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    public List<ReturnRequest> findAll() {
        return returns;
    }

    /** True if this product of this order already has a return that was not rejected. */
    public boolean exists(String orderId, String productId) {
        for (ReturnRequest r : returns) {
            if (r.getOrderId().equalsIgnoreCase(orderId) && r.getProductId().equalsIgnoreCase(productId)
                    && !r.getStatus().equals("Rejected")) {
                return true;
            }
        }
        return false;
    }
}
