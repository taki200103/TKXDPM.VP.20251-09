package com.hust.soict.aims.subsystems.paypal;

public class PayPalOrderResponse {
    private final String orderId;
    private final String approveUrl;

    public PayPalOrderResponse(String orderId, String approveUrl) {
        this.orderId = orderId;
        this.approveUrl = approveUrl;
    }

    public String getOrderId() { return orderId; }
    public String getApproveUrl() { return approveUrl; }
}
