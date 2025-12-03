package com.aims.subsystem;

import com.aims.exception.PaymentException;

public interface IQRCodePayment {
    /**
     * Tạo đường dẫn ảnh QR Code thanh toán
     * @param amount Số tiền cần thanh toán (VND)
     * @param content Nội dung chuyển khoản
     * @return String URL của ảnh QR
     * @throws PaymentException Nếu có lỗi xảy ra
     */
    String generatePayUrl(int amount, String content) throws PaymentException;
}