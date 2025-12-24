package com.hust.soict.aims.entities;

public class Invoice {
    private Order order;
    private double subtotal;
    private double shippingFee;
    private double total;

    public Invoice(Order order, double subtotal, double shippingFee) {
        this.order = order;
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.total = subtotal + shippingFee;
    }

    public Order getOrder() { return order; }
    public double getSubtotal() { return subtotal; }
    public double getShippingFee() { return shippingFee; }
    public double getTotal() { return total; }
}
