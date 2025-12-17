package com.aims.subsystem.vietqr;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class VietQRSubsystemController {
    // [SOLID VIOLATION]: SRP (Single Responsibility Principle) - Nguyên lý Đơn nhiệm
    // LÝ DO: Class Controller này có nhiệm vụ xử lý logic tạo URL.
    // Tuy nhiên, nó đang chứa cả DỮ LIỆU CẤU HÌNH (Config Data) như số tài khoản, tên ngân hàng.
    // Nếu muốn đổi số tài khoản nhận tiền, ta phải sửa class Controller -> Vi phạm SRP.

    // --- CẤU HÌNH TÀI KHOẢN CỦA PHAM MINH DAT ---

    // Mã ngân hàng VietinBank (ShortName là ICB hoặc Bin là 970415)
    private static final String BANK_ID = "ICB";

    // Số tài khoản (Lấy từ ảnh của bạn)
    private static final String ACCOUNT_NO = "109875430178";

    // Tên chủ tài khoản (Viết hoa không dấu)
    private static final String ACCOUNT_NAME = "PHAM MINH DAT";

    // Giao diện QR (compact là mẫu gọn đẹp nhất)
    private static final String TEMPLATE = "compact";

    /**
     * Tạo URL gọi sang VietQR Quick Link API
     */
    public String generateQRUrl(int amount, String content) throws UnsupportedEncodingException {
        // Encode nội dung để tránh lỗi URL
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