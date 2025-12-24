package com.hust.soict.aims.entities;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String id;
    private List<CartItem> items;
    private DeliveryInfo deliveryInfo;
    private double shippingFee;
    private LocalDateTime createdAt;

    public Order() { this.createdAt = LocalDateTime.now(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public DeliveryInfo getDeliveryInfo() { return deliveryInfo; }
    public void setDeliveryInfo(DeliveryInfo deliveryInfo) { this.deliveryInfo = deliveryInfo; }
    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
