package com.hust.soict.aims.entities;

/**
 * Entity representing payment status check result
 */
public class PaymentStatus {
    private String status;    // "PENDING", "COMPLETED", "FAILED", "CANCELLED"
    private String message;   // Additional message about payment status
    
    public PaymentStatus() {}
    
    public PaymentStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    // Helper methods
    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }
    
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }
    
    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(status);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }
    
    // Factory methods
    public static PaymentStatus parseResponseString(String response) {
        // Parse response from VietQR API
        // For now, simple implementation
        if (response == null || response.isEmpty()) {
            return new PaymentStatus("UNKNOWN", "Empty response");
        }
        
        // TODO: Parse actual JSON response from VietQR
        return new PaymentStatus("PENDING", "Payment is being processed");
    }
    
    @Override
    public String toString() {
        return String.format("PaymentStatus{status='%s', message='%s'}", status, message);
    }
}

