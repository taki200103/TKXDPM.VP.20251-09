package com.aims.subsystem.vietqr;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class VietQRSubsystemController {

    // CẤU HÌNH TÀI KHOẢN NHẬN TIỀN (Thay bằng số của bạn để test thật)
    private static final String BANK_ID = "MB"; // Ngân hàng: MB, VCB, ACB, TPB...
    private static final String ACCOUNT_NO = "1122334455"; // Số tài khoản người nhận
    private static final String TEMPLATE = "compact"; // Giao diện QR (compact, print, qr_only)
    private static final String ACCOUNT_NAME = "AIMS SHOP"; // Tên chủ tài khoản hiển thị

    /**
     * Tạo URL gọi sang VietQR Quick Link API
     * Docs: https://vietqr.io/danh-sach-api/api-quick-link/
     * Format: https://img.vietqr.io/image/<BANK_ID>-<ACCOUNT_NO>-<TEMPLATE>.png?amount=<AMOUNT>&addInfo=<CONTENT>&accountName=<NAME>
     */
    public String generateQRUrl(int amount, String content) throws UnsupportedEncodingException {
        // Encode nội dung (xử lý tiếng Việt và ký tự đặc biệt)
        String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
        String encodedName = URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8.toString());

        StringBuilder url = new StringBuilder();
        url.append("https://img.vietqr.io/image/");
        url.append(BANK_ID).append("-");
        url.append(ACCOUNT_NO).append("-");
        url.append(TEMPLATE).append(".png");
        url.append("?amount=").append(amount);
        url.append("&addInfo=").append(encodedContent);
        url.append("&accountName=").append(encodedName);

        return url.toString();
    }
}