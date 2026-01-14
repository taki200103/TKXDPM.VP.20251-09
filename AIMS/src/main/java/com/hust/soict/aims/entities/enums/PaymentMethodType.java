package com.hust.soict.aims.entities.enums;

public enum PaymentMethodType {
    QR_CODE,
    CREDIT_CARD,
    UNKNOWN;

    public static PaymentMethodType fromString(String s) {
        if (s == null) return UNKNOWN;
        String v = s.trim().toUpperCase();

        // hỗ trợ backward-compat cho dữ liệu cũ trong DB: "qr_code", "credit_card"
        if (v.equals("QR_CODE") || v.equals("QR-CODE") || v.equals("QR")) return QR_CODE;
        if (v.equals("CREDIT_CARD") || v.equals("CREDIT-CARD") || v.equals("CARD")) return CREDIT_CARD;

        try {
            return PaymentMethodType.valueOf(v);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}
