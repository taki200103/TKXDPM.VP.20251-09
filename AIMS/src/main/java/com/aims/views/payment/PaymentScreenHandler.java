package com.aims.views.payment;

import com.aims.controller.BaseController;
import com.aims.subsystem.paypal.PayPalSubsystem;
import com.aims.subsystem.vietqr.VietQRSubsystem;
import com.aims.views.BaseScreenHandler;
import com.aims.views.result.ResultScreenHandler; // Import màn hình kết quả
import com.aims.utils.Configs; // Import Configs để lấy đường dẫn file FXML

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class PaymentScreenHandler extends BaseScreenHandler {

    private static final Logger LOGGER = Logger.getLogger(PaymentScreenHandler.class.getName());

    private PayPalSubsystem payPalSubsystem;
    private VietQRSubsystem vietQRSubsystem;
    private Alert loadingAlert;

    public PaymentScreenHandler(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
        // [SOLID FIX - DIP]: Khởi tạo Subsystem tại Handler thay vì tại View
        this.payPalSubsystem = new PayPalSubsystem();
        this.vietQRSubsystem = new VietQRSubsystem();
    }

    /**
     * Hàm xử lý chính cho nút "Thanh toán PayPal":
     * 1. Lấy URL
     * 2. Mở trình duyệt
     * 3. Chờ 10s giả lập
     * 4. Chuyển màn hình
     */
    public void requestPayment(int amount, String contents) {
        try {
            // 1. Gọi Subsystem để lấy Link thanh toán
            String payUrl = payPalSubsystem.generatePayUrl(amount, contents);

            if (payUrl == null) {
                showError("Không thể tạo đơn hàng PayPal!");
                return;
            }

            // 2. Mở trình duyệt (Logic hệ thống được tách khỏi View -> Tốt cho SRP)
            openBrowser(payUrl);

            // 3. Hiển thị thông báo chờ để User biết
            showLoadingAlert();

            // 4. [SIMULATION DEMO] Tạo luồng riêng để chờ PayPal phản hồi giả định
            new Thread(() -> {
                try {
                    LOGGER.info("Đang chờ người dùng thanh toán trên Browser...");

                    // Giả vờ đợi 5-10 giây (Thời gian User đăng nhập & bấm Pay)
                    Thread.sleep(8000);

                    // --- KẾT THÚC GIẢ LẬP ---

                    // Dùng Platform.runLater để update UI từ luồng khác
                    Platform.runLater(() -> {
                        LOGGER.info("Giả lập: Nhận được tín hiệu thành công từ PayPal!");
                        onPaymentSuccess(amount, contents);
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            showError("Lỗi hệ thống: " + e.getMessage());
        }
    }

    /**
     * Logic lấy mã QR (View gọi hàm này để hiển thị ảnh)
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
     * Xử lý chuyển màn hình khi thành công
     */
    private void onPaymentSuccess(int amount, String contents) {
        Platform.runLater(() -> {
            try {
                LOGGER.info("Đã đóng màn hình chờ và hiển thị kết quả thành công.");

                // 🟢 Đóng popup nếu còn mở
                if (loadingAlert != null) {
                    loadingAlert.close();   // hoặc loadingAlert.hide();
                    loadingAlert = null;
                }

                // Mở màn hình kết quả
                ResultScreenHandler resultScreen =
                        new ResultScreenHandler(this.stage, Configs.RESULT_SCREEN_PATH);

                resultScreen.show();
                resultScreen.showResult("PAYMENT SUCCESS");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoadingAlert() {
        // dùng field, KHÔNG thêm chữ 'Alert' ở trước nữa
        loadingAlert = new Alert(Alert.AlertType.INFORMATION);
        loadingAlert.setTitle("Đang xử lý thanh toán");
        loadingAlert.setHeaderText("Vui lòng thực hiện thanh toán trên trình duyệt");
        loadingAlert.setContentText("Hệ thống đang chờ phản hồi từ PayPal...\n(Cửa sổ này sẽ tự đóng khi hoàn tất)");
        loadingAlert.initOwner(this.stage); // cho đẹp, gắn cùng window
        loadingAlert.show();                // dùng show() chứ không showAndWait()
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setContentText(msg);
        alert.show();
    }

    // Hàm hỗ trợ kế thừa
    public void setBController(BaseController bController){
    }
}