package com.hust.soict.aims.entities;

import com.hust.soict.aims.entities.enums.PaymentState;

public class PaymentStatus {
    private PaymentState state;
    private String message;

    public PaymentStatus() {
        this.state = PaymentState.UNKNOWN;
    }

    public PaymentStatus(PaymentState state, String message) {
        this.state = state != null ? state : PaymentState.UNKNOWN;
        this.message = message;
    }

    public PaymentState getState() { return state; }
    public void setState(PaymentState state) { this.state = state != null ? state : PaymentState.UNKNOWN; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isCompleted() { return state == PaymentState.COMPLETED; }
    public boolean isPending() { return state == PaymentState.PENDING; }
    public boolean isFailed() { return state == PaymentState.FAILED; }
    public boolean isCancelled() { return state == PaymentState.CANCELLED; }

    public static PaymentStatus fromProviderResponse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new PaymentStatus(PaymentState.UNKNOWN, "Empty response");
        }
        // Nếu bạn parse JSON VietQR/PayPal thì parse ở đây
        return new PaymentStatus(PaymentState.PENDING, "Payment is being processed");
    }

    @Override
    public String toString() {
        return "PaymentStatus{state=" + state + ", message='" + message + "'}";
    }
}
