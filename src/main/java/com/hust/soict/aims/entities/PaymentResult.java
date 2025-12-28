package com.hust.soict.aims.entities;

import com.hust.soict.aims.entities.enums.PaymentMethod;

/**
 * Result object returned after creating a payment
 * Used by UI layer (PaymentScreen) to display payment info
 */
public class PaymentResult {

    private final PaymentMethod method;

    // Dùng cho PayPal
    private final String payUrl;

    // Dùng cho VietQR
    private final QRCode qrCode;

    // ===== Constructors =====

    /**
     * Constructor cho PayPal payment
     */
    public static PaymentResult paypal(String payUrl) {
        return new PaymentResult(PaymentMethod.PAYPAL, payUrl, null);
    }

    /**
     * Constructor cho VietQR payment
     */
    public static PaymentResult vietQR(QRCode qrCode) {
        return new PaymentResult(PaymentMethod.VIETQR, null, qrCode);
    }

    private PaymentResult(PaymentMethod method, String payUrl, QRCode qrCode) {
        this.method = method;
        this.payUrl = payUrl;
        this.qrCode = qrCode;
    }

    // ===== Getters =====

    public PaymentMethod getMethod() {
        return method;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public QRCode getQrCode() {
        return qrCode;
    }
}
