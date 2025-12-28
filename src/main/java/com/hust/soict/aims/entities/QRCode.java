package com.hust.soict.aims.entities;

/**
 * Entity representing QR code information for payment
 */
public class QRCode {
    private String qrCode;        // Base64 or URL of QR code image
    private String qrLink;        // Link to view QR code
    private String bankCode;      // Bank code (e.g., "VCB", "TCB")
    private String bankName;      // Bank name (e.g., "Vietcombank")
    private String bankAccount;   // Bank account number
    
    public QRCode() {}
    
    public QRCode(String qrCode, String qrLink, String bankCode, String bankName, String bankAccount) {
        this.qrCode = qrCode;
        this.qrLink = qrLink;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.bankAccount = bankAccount;
    }
    
    // Getters and Setters
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    
    public String getQrLink() { return qrLink; }
    public void setQrLink(String qrLink) { this.qrLink = qrLink; }
    
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    
    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    
    @Override
    public String toString() {
        return String.format("QRCode{bankName='%s', bankCode='%s', account='%s'}", 
            bankName, bankCode, bankAccount);
    }
}

