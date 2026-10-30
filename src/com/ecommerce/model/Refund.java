package com.ecommerce.model;

import java.time.LocalDate;


public class Refund {
    private String id;
    private String orderId;
    private String customerId;
    private double amount;
    private String paymentMethod;   
    private LocalDate date;
    private String reason;

    public Refund(String id, String orderId, String customerId, double amount,
                  String paymentMethod, LocalDate date, String reason) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.reason = reason;
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDate getDate() { return date; }
    public String getReason() { return reason; }
}
