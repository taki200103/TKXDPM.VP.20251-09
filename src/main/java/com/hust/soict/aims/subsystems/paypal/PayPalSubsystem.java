package com.hust.soict.aims.subsystems.paypal;

import com.hust.soict.aims.exceptions.PaymentException;
import com.hust.soict.aims.subsystems.IQRCodePayment;

public class PayPalSubsystem implements IQRCodePayment {

    private PayPalSubsystemController ctrl;

    public PayPalSubsystem() {
        //
        // [SOLID VIOLATION]: DIP (Dependency Inversion Principle) - Nguyên lý Đảo ngược
        // sự phụ thuộc
        // LÝ DO: Class này (High-level module trong Subsystem) đang phụ thuộc trực tiếp
        // vào
        //
        //
        //
        // HẬU QUẢ: Code bị dính chặt (Tight Coupling). Khó mở rộng, khó viết Unit Test
        // (không thể Mock controller).
        // GIẢI PHÁP: Nên sử dụng Dependency Injection (tiêm Controller vào qua Constru
        // tor).
        this.ctrl = new PayPalSubsystemController();
        //
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

    //
    // LÝ DO: Tương tự như VietQR, class này bị ép buộc phải implement phương thứ
    // Mặc dù PayPal có hỗ trợ hoàn tiền, nhưng việc gộp chung 2 tính năng "Thanh
    // oán" và "Hoàn tiền"
    // vào chung 1 interface khiến cho các class con mất đi sự linh hoạt.
    //
    @Override
    public String refund(int amount, String content) throws PaymentException {
        return "Yêu cầu hoàn tiền PayPal thành công (Demo)";
    }
}