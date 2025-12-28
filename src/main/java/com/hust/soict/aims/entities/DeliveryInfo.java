package com.hust.soict.aims.entities;

public class DeliveryInfo {
    private long orderId; // Primary key in DB
    private String recipientName; // recipient_name in DB
    private String phoneNumber; // phone_number in DB
    private String email;
    private String deliveryAddress; // delivery_address in DB
    private String city;
    private String instructions;
    
    // Legacy fields for backward compatibility
    private String receiverName; // Maps to recipientName
    private String phone; // Maps to phoneNumber
    private String district; // Not in DB, can be part of address
    private String addressLine; // Maps to deliveryAddress

    public DeliveryInfo() {}

    // New getters/setters matching DB schema
    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }
    public String getRecipientName() { 
        return recipientName != null ? recipientName : receiverName; 
    }
    public void setRecipientName(String recipientName) { 
        this.recipientName = recipientName;
        this.receiverName = recipientName; // Sync with legacy field
    }
    public String getPhoneNumber() { 
        return phoneNumber != null ? phoneNumber : phone; 
    }
    public void setPhoneNumber(String phoneNumber) { 
        this.phoneNumber = phoneNumber;
        this.phone = phoneNumber; // Sync with legacy field
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDeliveryAddress() { 
        return deliveryAddress != null ? deliveryAddress : addressLine; 
    }
    public void setDeliveryAddress(String deliveryAddress) { 
        this.deliveryAddress = deliveryAddress;
        this.addressLine = deliveryAddress; // Sync with legacy field
    }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    // Legacy getters/setters for backward compatibility
    public String getReceiverName() { 
        return receiverName != null ? receiverName : recipientName; 
    }
    public void setReceiverName(String receiverName) { 
        this.receiverName = receiverName;
        this.recipientName = receiverName; // Sync with new field
    }
    public String getPhone() { 
        return phone != null ? phone : phoneNumber; 
    }
    public void setPhone(String phone) { 
        this.phone = phone;
        this.phoneNumber = phone; // Sync with new field
    }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getAddressLine() { 
        return addressLine != null ? addressLine : deliveryAddress; 
    }
    public void setAddressLine(String addressLine) { 
        this.addressLine = addressLine;
        this.deliveryAddress = addressLine; // Sync with new field
    }
}
