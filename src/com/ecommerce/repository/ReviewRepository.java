package com.ecommerce.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.ecommerce.model.Review;

/** In-memory storage for reviews and products flagged for admin review. */
public class ReviewRepository {
    private List<Review> reviews = new ArrayList<>();
    private Set<String> flaggedProductIds = new HashSet<>();
    private int counter = 1;

    public String nextId() {
        return "RV" + counter++;
    }

    public void save(Review r) {
        reviews.add(r);
    }

    public List<Review> findByProduct(String productId) {
        List<Review> result = new ArrayList<>();
        for (Review r : reviews) {
            if (r.getProductId().equalsIgnoreCase(productId)) result.add(r);
        }
        return result;
    }

    public boolean exists(String orderId, String productId) {
        for (Review r : reviews) {
            if (r.getOrderId().equalsIgnoreCase(orderId) && r.getProductId().equalsIgnoreCase(productId)) {
                return true;
            }
        }
        return false;
    }

    public void flag(String productId) {
        flaggedProductIds.add(productId.toUpperCase());
    }

    public List<String> findFlagged() {
        return new ArrayList<>(flaggedProductIds);
    }
}
