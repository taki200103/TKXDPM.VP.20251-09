package com.hust.soict.aims.subsystems.vietqr;

import com.hust.soict.aims.exceptions.PaymentException;
import com.hust.soict.aims.subsystems.IQRCodePayment;

public class VietQRSubsystem implements IQRCodePayment {

    private VietQRSubsystemController ctrl;

    public VietQRSubsystem() {
        this.ctrl = new VietQRSubsystemController();
    }

    @Override
    public String generatePayUrl(int amount, String content) throws PaymentException {
        try {
            return this.ctrl.generateQRUrl(amount, content);
        } catch (Exception e) {
            throw new PaymentException("Lỗi khi tạo mã VietQR: " + e.getMessage());
        }
    }

    // [SOLID VIOLATION]: ISP (Interface Segregation Principle) - Nguyên lý Phân
    // tách Interface
    // LÝ DO: Interface IQRCodePayment đang ép buộc class này phải implement hàm
    // refund().
    // Tuy nhiên, VietQR (với tài khoản cá nhân) không hỗ trợ API hoàn tiền tự động.
    // Class này buộc phải viết một hàm "giả" (dummy implementation) vô nghĩa hoặc
    // ném lỗi.
    // -> Interface IQRCodePayment đang quá lớn (Fat Interface).
    @Override
    public String refund(int amount, String content) throws PaymentException {
        // VietQR cá nhân không có API hoàn tiền tự động.
        // Ta giả lập hành động này hoặc thông báo cho Admin.
        System.out.println("VietQR: Đang thực hiện hoàn tiền thủ công cho số tiền " + amount);

        // Trả về thông báo thành công giả định để hệ thống AIMS ghi nhận hủy đơn
        return "Yêu cầu hoàn tiền thành công (VietQR - Manual Process)";
    }
}