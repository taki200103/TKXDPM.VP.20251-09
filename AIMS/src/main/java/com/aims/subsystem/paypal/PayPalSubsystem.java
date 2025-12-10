package com.aims.subsystem.paypal;

import com.aims.exception.PaymentException;
import com.aims.subsystem.IQRCodePayment;

public class PayPalSubsystem implements IQRCodePayment {

    private PayPalSubsystemController ctrl;

    public PayPalSubsystem() {
        this.ctrl = new PayPalSubsystemController();
    }

    @Override
    public String generatePayUrl(int amount, String content) throws PaymentException {
        try {
            // Gọi Controller để lấy link thanh toán thật từ API
            return ctrl.createOrder(amount);
        } catch (Exception e) {
            throw new PaymentException("Lỗi PayPal: " + e.getMessage());
        }
    }

    @Override
    public String refund(int amount, String content) throws PaymentException {
        // Có thể implement gọi API Refund ở đây nếu muốn
        return "Yêu cầu hoàn tiền PayPal thành công (Demo)";
    }
}