package com.hust.soict.aims.entities;
// de luu lai don hang 

public class OrderMedia {
    private long orderId;
    private long mediaId;
    private int quantity;
    private double price;

    public OrderMedia() {}

    public OrderMedia(long orderId, long mediaId, int quantity, double price) {
        this.orderId = orderId;
        this.mediaId = mediaId;
        this.quantity = quantity;
        this.price = price;
    }

    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }
    public long getMediaId() { return mediaId; }
    public void setMediaId(long mediaId) { this.mediaId = mediaId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
