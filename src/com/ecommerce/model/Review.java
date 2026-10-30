package com.ecommerce.model;

/** A rating (1 to 5) given by a customer for a product of an order. */
public class Review {
    private String id;
    private String orderId;
    private String productId;
    private String customerId;
    private int rating;
    private String comment;

    public Review(String id, String orderId, String productId, String customerId, int rating, String comment) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.customerId = customerId;
        setRating(rating);
        this.comment = comment;
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getProductId() { return productId; }
    public String getCustomerId() { return customerId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }

    private void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }
}
