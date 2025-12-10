package com.aims.subsystem;

import com.aims.exception.PaymentException;

public interface IQRCodePayment {
    /**
     * Tạo đường dẫn ảnh QR Code thanh toán
     */
    String generatePayUrl(int amount, String content) throws PaymentException;

    /**
     * Hoàn tiền cho giao dịch (Dùng khi hủy đơn hàng)
     * @param amount Số tiền cần hoàn
     * @param content Nội dung hoàn tiền
     * @return Thông báo kết quả hoàn tiền
     */
    String refund(int amount, String content) throws PaymentException;
}