package com.hust.soict.aims.entities.enums;

public enum PaymentState {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED,
    UNKNOWN;

    public static PaymentState fromString(String s) {
        if (s == null) return UNKNOWN;
        try {
            return PaymentState.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}
