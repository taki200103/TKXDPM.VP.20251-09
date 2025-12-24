package com.hust.soict.aims.utils;

import com.hust.soict.aims.controls.PayByCreditCardController;
import com.hust.soict.aims.subsystems.vietqr.VietQRController;
import com.hust.soict.aims.controls.IPaymentQRCode;

/**
 * Service Locator Pattern
 * Provides centralized access to application services
 */
public class ServiceProvider {
    private static ServiceProvider instance;
    
    private PayByCreditCardController creditCardController;
    private IPaymentQRCode qrPaymentController;
    
    private ServiceProvider() {}
    
    /**
     * Get singleton instance
     */
    public static ServiceProvider getInstance() {
        if (instance == null) {
            instance = new ServiceProvider();
        }
        return instance;
    }
    
    /**
     * Initialize with payment controllers
     * Should be called once at application startup
     * @param creditCardController Credit card payment controller (from Spring)
     */
    public void initialize(PayByCreditCardController creditCardController) {
        this.creditCardController = creditCardController;
        
        // Initialize QR payment controller with credentials and bank account info from config
        String vietqrUsername = ConfigLoader.getVietQRUsername();
        String vietqrPassword = ConfigLoader.getVietQRPassword();
        String bankCode = ConfigLoader.getVietQRBankCode();
        String bankAccount = ConfigLoader.getVietQRBankAccount();
        String userBankName = ConfigLoader.getVietQRUserBankName();
        
        this.qrPaymentController = new VietQRController(
            vietqrUsername, vietqrPassword, bankCode, bankAccount, userBankName
        );
    }
    
    /**
     * Get credit card payment controller
     * @return PayByCreditCardController for PayPal integration
     */
    public PayByCreditCardController getCreditCardController() {
        if (creditCardController == null) {
            throw new IllegalStateException("ServiceProvider not initialized! Call initialize() first.");
        }
        return creditCardController;
    }
    
    /**
     * Get QR code payment controller
     * @return IPaymentQRCode implementation (VietQR)
     */
    public IPaymentQRCode getQRPaymentController() {
        if (qrPaymentController == null) {
            throw new IllegalStateException("ServiceProvider not initialized! Call initialize() first.");
        }
        return qrPaymentController;
    }
}

