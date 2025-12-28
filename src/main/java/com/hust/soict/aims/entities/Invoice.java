package com.hust.soict.aims.entities;

import java.sql.Timestamp;

public class Invoice {
    private long invoiceId;
    private long orderId; // UNIQUE
    private long paymentTransactionId; // UNIQUE
    private double productTotal; // product_total in DB
    private double vatAmount;
    private double shippingFee;
    private double totalAmount; // total_amount in DB
    private Timestamp createdAt;
    
    // Legacy fields for backward compatibility
    private Order order; // Can be loaded from orderId
    private double subtotal; // Maps to productTotal

    public Invoice() {}

    public Invoice(Order order, double subtotal, double shippingFee) {
        this.order = order;
        this.subtotal = subtotal;
        this.productTotal = subtotal;
        this.shippingFee = shippingFee;
        this.totalAmount = subtotal + shippingFee;
        if (order != null) {
            this.orderId = order.getOrderId();
        }
    }

    // New getters/setters matching DB schema
    public long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(long invoiceId) { this.invoiceId = invoiceId; }
    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { 
        this.orderId = orderId;
        if (order != null) {
            order.setOrderId(orderId);
        }
    }
    public long getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(long paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
    public double getProductTotal() { 
        return productTotal > 0 ? productTotal : subtotal; 
    }
    public void setProductTotal(double productTotal) { 
        this.productTotal = productTotal;
        this.subtotal = productTotal; // Sync with legacy field
    }
    public double getVatAmount() { return vatAmount; }
    public void setVatAmount(double vatAmount) { this.vatAmount = vatAmount; }
    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public double getTotalAmount() { 
        if (totalAmount > 0) return totalAmount;
        return productTotal + shippingFee + vatAmount;
    }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    // Legacy getters/setters for backward compatibility
    public Order getOrder() { return order; }
    public void setOrder(Order order) { 
        this.order = order;
        if (order != null) {
            this.orderId = order.getOrderId();
        }
    }
    public double getSubtotal() { 
        return subtotal > 0 ? subtotal : productTotal; 
    }
    public void setSubtotal(double subtotal) { 
        this.subtotal = subtotal;
        this.productTotal = subtotal; // Sync with new field
    }
    public double getTotal() { return getTotalAmount(); }
}
