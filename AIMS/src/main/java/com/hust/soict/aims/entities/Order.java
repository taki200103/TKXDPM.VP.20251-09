package com.hust.soict.aims.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private long orderId; // INTEGER in DB
    private String status; // pending | approved | rejected | cancelled
    private Integer processedBy; // user_id
    private Timestamp processedAt;
    private String rejectReason;
    private Timestamp createdAt;
    
    // Legacy fields for backward compatibility
    private String id; // Legacy - maps to orderId
    private List<CartItem> items;
    private DeliveryInfo deliveryInfo;
    private double shippingFee;
    private LocalDateTime createdDateTime; // Legacy

    public Order() { 
        this.status = "pending";
        this.createdDateTime = LocalDateTime.now();
    }

    // New getters/setters
    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { 
        this.orderId = orderId;
        this.id = String.valueOf(orderId); // Sync with legacy field
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getProcessedBy() { return processedBy; }
    public void setProcessedBy(Integer processedBy) { this.processedBy = processedBy; }
    public Timestamp getProcessedAt() { return processedAt; }
    public void setProcessedAt(Timestamp processedAt) { this.processedAt = processedAt; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { 
        this.createdAt = createdAt;
        if (createdAt != null) {
            this.createdDateTime = createdAt.toLocalDateTime();
        }
    }
    
    // Legacy getters/setters for backward compatibility
    public String getId() { 
        if (id == null && orderId > 0) {
            id = String.valueOf(orderId);
        }
        return id; 
    }
    public void setId(String id) { 
        this.id = id;
        if (id != null && !id.isEmpty()) {
            try {
                this.orderId = Long.parseLong(id);
            } catch (NumberFormatException ignored) {}
        }
    }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public DeliveryInfo getDeliveryInfo() { return deliveryInfo; }
    public void setDeliveryInfo(DeliveryInfo deliveryInfo) { this.deliveryInfo = deliveryInfo; }
    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public LocalDateTime getCreatedDateTime() { 
        if (createdDateTime == null && createdAt != null) {
            createdDateTime = createdAt.toLocalDateTime();
        }
        return createdDateTime; 
    }
    public void setCreatedDateTime(LocalDateTime createdDateTime) { 
        this.createdDateTime = createdDateTime;
        if (createdDateTime != null) {
            this.createdAt = Timestamp.valueOf(createdDateTime);
        }
    }
}
