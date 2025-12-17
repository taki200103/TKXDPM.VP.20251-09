package com.aims.views.payment;

import com.aims.controller.BaseController;
import com.aims.subsystem.paypal.PayPalSubsystem;
import com.aims.subsystem.vietqr.VietQRSubsystem;
import com.aims.views.BaseScreenHandler;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class PaymentScreenHandler extends BaseScreenHandler {

    private static final Logger LOGGER = Logger.getLogger(PaymentScreenHandler.class.getName());

    // [SOLID FIX]: DIP (Dependency Inversion Principle)
    // Thay vì để View (PaymentForm) khởi tạo trực tiếp Subsystem, ta đưa việc đó vào Controller/Handler.
    // View sẽ không còn phụ thuộc vào PayPalSubsystem hay VietQRSubsystem nữa.
    // Controller nắm giữ các Subsystem để xử lý logic, không để View nắm giữ.
    private PayPalSubsystem payPalSubsystem;
    private VietQRSubsystem vietQRSubsystem;

    public PaymentScreenHandler(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
        // [SOLID NOTE]: Để đạt điểm tuyệt đối về DIP, ở đây nên dùng Dependency Injection (tiêm vào qua constructor).
        // Tuy nhiên, khởi tạo tại đây (Composition) đã tốt hơn rất nhiều so với khởi tạo tại View.
        // (Trong thực tế nâng cao, các subsystem này nên được Inject vào từ bên ngoài để đảm bảo DIP tuyệt đối)
        this.payPalSubsystem = new PayPalSubsystem();
        this.vietQRSubsystem = new VietQRSubsystem();
    }

    /**
     * Logic lấy URL QR Code cho VietQR
     * View chỉ cần gọi hàm này, không cần biết VietQRSubsystem là gì.
     */
    public String getVietQRCode(int amount, String content) {
        try {
            return vietQRSubsystem.generatePayUrl(amount, content);
        } catch (Exception e) {
            LOGGER.info("Error generating VietQR: " + e.getMessage());
            return null;
        }
    }

    /**
     * [SOLID FIX]: SRP (Single Responsibility Principle)
     * Tương tự, logic xử lý PayPal được đóng gói tại đây.
     * Nếu sau này PayPal đổi API, ta chỉ sửa ở Subsystem và Handler, giao diện PaymentForm không bị ảnh hưởng.
     */
    public String createPayPalOrder(int amount) {
        try {
            // Gọi xuống Subsystem để lấy Link thanh toán
            return payPalSubsystem.generatePayUrl(amount, "Payment Transaction");
        } catch (Exception e) {
            LOGGER.info("Error creating PayPal order: " + e.getMessage());
            return null;
        }
    }

    // Hàm hỗ trợ trống để kế thừa BaseScreenHandler (nếu cần)
    public void setBController(BaseController bController){
    }
}