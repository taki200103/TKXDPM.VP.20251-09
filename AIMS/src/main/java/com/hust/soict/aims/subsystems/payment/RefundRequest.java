package com.hust.soict.aims.subsystems.payment;

/**
 * DTO yêu cầu hoàn tiền (nếu provider hỗ trợ)
 */
public class RefundRequest {
    private final long amount;
    private final String reason;
    private final String providerTransactionId; // orderId/transactionId bên cổng thanh toán

    public RefundRequest(long amount, String reason, String providerTransactionId) {
        this.amount = amount;
        this.reason = reason;
        this.providerTransactionId = providerTransactionId;
    }

    public long getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public String getProviderTransactionId() {
        return providerTransactionId;
    }
}
