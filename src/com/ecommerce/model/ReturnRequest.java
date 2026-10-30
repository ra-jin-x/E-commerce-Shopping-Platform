package com.ecommerce.model;

/** A request to return one product of a delivered order. */
public class ReturnRequest {
    private String id;
    private String orderId;
    private String productId;
    private String productName;
    private String category;
    private String customerId;
    private String paymentMethod;
    private int quantity;
    private double amount;            
    private String reason;
    private String status;           

    public ReturnRequest(String id, String orderId, String productId, String productName, String category,
                         String customerId, String paymentMethod, int quantity, double amount, String reason) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.customerId = customerId;
        this.paymentMethod = paymentMethod;
        this.quantity = quantity;
        this.amount = amount;
        this.reason = reason;
        this.status = "Requested";
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getCategory() { return category; }
    public String getCustomerId() { return customerId; }
    public String getPaymentMethod() { return paymentMethod; }
    public int getQuantity() { return quantity; }
    public double getAmount() { return amount; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }

    public void reject() {
        this.status = "Rejected";
    }

    public void approve() {
        this.status = "Approved";
    }
}
