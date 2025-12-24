package com.hust.soict.aims.subsystems.vietqr;

/**
 * Request for generating VietQR code
 * Based on official API documentation: https://api.vietqr.vn/vi/api-vietqr-callback/goi-api-generate-vietqr-code
 */
public class QRGenerateRequest {
    private String bankCode;
    private String bankAccount;
    private String userBankName;
    private String content;
    private int qrType; // qrType: 0=dynamic, 1=static, 3=semi-dynamic
    private long amount;
    private String orderId;
    private String transType;
    
    /**
     * Constructor for dynamic QR code (qrType = 0)
     */
    public QRGenerateRequest(String bankCode, String bankAccount, String userBankName,
                             String content, long amount, String orderId) {
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.content = sanitizeContent(content);
        this.amount = amount;
        this.orderId = sanitizeOrderId(orderId);
        this.qrType = 0;  // Dynamic QR
        this.transType = "C";  // Credit (receiving money)
    }
    
    public String buildRequestString() {
        // Build JSON request exactly as VietQR API specification
        // https://api.vietqr.vn/vi/api-vietqr-callback/goi-api-generate-vietqr-code
        return String.format(
            "{\"bankCode\":\"%s\",\"bankAccount\":\"%s\",\"userBankName\":\"%s\"," +
            "\"content\":\"%s\",\"qrType\":%d,\"amount\":%d,\"orderId\":\"%s\",\"transType\":\"%s\"}",
            bankCode, bankAccount, userBankName, 
            content, qrType, amount, orderId, transType
        );
    }
    
    /**
     * Sanitize content to meet VietQR requirements:
     * - Max 23 characters
     * - No special characters
     * - Vietnamese without diacritics
     */
    private String sanitizeContent(String content) {
        if (content == null) return "";
        
        // Remove diacritics and special characters
        String sanitized = content.replaceAll("[^a-zA-Z0-9 ]", "");
        
        // Truncate to 23 chars
        if (sanitized.length() > 23) {
            sanitized = sanitized.substring(0, 23);
        }
        
        return sanitized;
    }
    
    /**
     * Sanitize order ID to meet VietQR requirements:
     * - Max 13 characters
     * - No special characters
     */
    private String sanitizeOrderId(String orderId) {
        if (orderId == null) return "";
        
        // Remove special characters
        String sanitized = orderId.replaceAll("[^a-zA-Z0-9]", "");
        
        // Truncate to 13 chars
        if (sanitized.length() > 13) {
            sanitized = sanitized.substring(0, 13);
        }
        
        return sanitized;
    }
    
    // Getters
    public String getBankCode() { return bankCode; }
    public String getBankAccount() { return bankAccount; }
    public String getUserBankName() { return userBankName; }
    public String getContent() { return content; }
    public int getQrType() { return qrType; }
    public long getAmount() { return amount; }
    public String getOrderId() { return orderId; }
    public String getTransType() { return transType; }
}
