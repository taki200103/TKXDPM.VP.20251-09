package com.hust.soict.aims.entities;

import com.hust.soict.aims.entities.enums.PaymentMethodType;

import java.sql.Timestamp;

public class PaymentTransaction {
    private long paymentTransactionId;
    private double amount;
    private PaymentMethodType methodType = PaymentMethodType.UNKNOWN;
    private String transactionNo;
    private String transactionContent;
    private Timestamp payDate;
    private String bankCode;
    private String bankTransactionNo;
    private String cardType;

    public PaymentTransaction() {}

    public long getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(long paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public PaymentMethodType getMethodType() { return methodType; }
    public void setMethodType(PaymentMethodType methodType) {
        this.methodType = methodType != null ? methodType : PaymentMethodType.UNKNOWN;
    }

    // 👇 HỖ TRỢ LEGACY DB
    public void setMethodType(String methodType) {
        this.methodType = PaymentMethodType.fromString(methodType);
    }

    // 👇 Xuất ra dạng DB hiểu
    public String getMethodTypeAsString() {
        return methodType.name().toLowerCase(); // qr_code, credit_card
    }

    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }

    public String getTransactionContent() { return transactionContent; }
    public void setTransactionContent(String transactionContent) { this.transactionContent = transactionContent; }

    public Timestamp getPayDate() { return payDate; }
    public void setPayDate(Timestamp payDate) { this.payDate = payDate; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }

    public String getBankTransactionNo() { return bankTransactionNo; }
    public void setBankTransactionNo(String bankTransactionNo) { this.bankTransactionNo = bankTransactionNo; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }
}
