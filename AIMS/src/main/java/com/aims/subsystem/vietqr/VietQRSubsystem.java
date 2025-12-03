package com.aims.subsystem.vietqr;

import com.aims.exception.PaymentException;
import com.aims.subsystem.IQRCodePayment;
import java.io.IOException;

public class VietQRSubsystem implements IQRCodePayment {

    private VietQRSubsystemController ctrl;

    public VietQRSubsystem() {
        this.ctrl = new VietQRSubsystemController();
    }

    @Override
    public String generatePayUrl(int amount, String content) throws PaymentException {
        try {
            // Gọi controller để xử lý logic tạo link
            return this.ctrl.generateQRUrl(amount, content);
        } catch (IOException e) {
            throw new PaymentException("Lỗi khi tạo mã VietQR: " + e.getMessage());
        }
    }
}