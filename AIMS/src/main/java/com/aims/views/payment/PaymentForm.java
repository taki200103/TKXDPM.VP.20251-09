package com.aims.views.payment;

import com.aims.views.BaseForm;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.util.Locale;
// [SOLID VIOLATION]: SRP (Single Responsibility Principle) - Nguyên lý Đơn nhiệm
// LÝ DO: Class PaymentForm là một lớp giao diện (View), nhiệm vụ duy nhất của nó nên là hiển thị UI.
// Tuy nhiên, hiện tại class này đang có độ Cohesion (tính liên kết) thấp vì nó chứa cả logic nghiệp vụ:
// 1. Khởi tạo Subsystem thanh toán.
// 2. Gọi API lấy link thanh toán.
// 3. Xử lý luồng mở trình duyệt và chờ phản hồi.
// -> Class này đang có quá nhiều lý do để thay đổi (sửa giao diện cũng phải vào đây, sửa logic thanh toán cũng phải vào đây).
public class PaymentForm extends BaseForm {

    private int amount;
    private String transactionContent;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    // [QUAN TRỌNG] Biến Handler để xử lý logic
    private PaymentScreenHandler handler;

    // [QUAN TRỌNG] Constructor nhận 3 tham số để khớp với HomeForm
    public PaymentForm(int amount, String content, PaymentScreenHandler handler) throws IOException {
        super();
        this.amount = amount;
        this.transactionContent = content;
        this.handler = handler; // Lưu handler lại
        initializeUI();
    }

    private void initializeUI() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Thanh toán đơn hàng");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        TabPane paymentMethods = new TabPane();
        paymentMethods.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        paymentMethods.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1px;");

        // [SOLID VIOLATION]: OCP (Open/Closed Principle) - Nguyên lý Đóng/Mở
        // LÝ DO: Việc khởi tạo các Tab thanh toán đang bị "Hard-code" (mã cứng) ngay tại đây.
        // Yêu cầu bổ sung (Additional Requirement): Nếu trong tương lai cần thêm phương thức thanh toán mới (ví dụ ZaloPay, MoMo),
        // ta bắt buộc phải mở file PaymentForm.java ra để sửa đổi hàm này -> Vi phạm nguyên tắc "Đóng với việc sửa đổi".
        Tab qrTab = new Tab("Thanh toán qua VietQR");
        qrTab.setContent(createVietQRContent());

        Tab creditTab = new Tab("Thẻ tín dụng (PayPal)");
        creditTab.setContent(createPayPalContent());

        paymentMethods.getTabs().addAll(qrTab, creditTab);

        Button cancelButton = new Button("Quay lại");
        cancelButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px;");
        cancelButton.setOnAction(e -> {
            ((Stage) this.content.getScene().getWindow()).close();
        });

        mainContainer.getChildren().addAll(titleLabel, paymentMethods, cancelButton);

        this.content.getChildren().add(mainContainer);
        AnchorPane.setTopAnchor(mainContainer, 0.0);
        AnchorPane.setBottomAnchor(mainContainer, 0.0);
        AnchorPane.setLeftAnchor(mainContainer, 0.0);
        AnchorPane.setRightAnchor(mainContainer, 0.0);
    }

    private VBox createVietQRContent() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        Label guide = new Label("Quét mã để thanh toán (VietQR API):");
        guide.setFont(Font.font("Arial", 14));

        ImageView qrView = new ImageView();
        qrView.setFitWidth(300);
        qrView.setFitHeight(300);
        qrView.setPreserveRatio(true);
        qrView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1px;");

        Label statusLabel = new Label("Đang tải mã QR...");
        statusLabel.setTextFill(Color.BLUE);

        try {
            // [SOLID VIOLATION]: DIP (Dependency Inversion Principle) - Nguyên lý Đảo ngược sự phụ thuộc
            // LÝ DO: Module cấp cao (PaymentForm - Giao diện) đang phụ thuộc trực tiếp vào Module cấp thấp (VietQRSubsystem - Chi tiết triển khai).
            // Hậu quả: Không thể thay thế VietQRSubsystem bằng một Mock Object để Unit Test giao diện độc lập được.
            // Code bị dính chặt (Tight Coupling).
            // SỬA: Gọi qua Handler thay vì new Subsystem
            String qrUrl = handler.getVietQRCode(this.amount, this.transactionContent);

            if (qrUrl != null) {
                Image image = new Image(qrUrl, true);
                qrView.setImage(image);
                statusLabel.setText("Đã tạo mã QR. Vui lòng quét.");
                statusLabel.setTextFill(Color.GREEN);
            } else {
                statusLabel.setText("Không thể tạo QR Code.");
                statusLabel.setTextFill(Color.RED);
            }

        } catch (Exception e) {
            statusLabel.setText("Lỗi: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }

        String formattedPrice = currencyFormatter.format(this.amount).replace("₫", "VND");
        Label totalLabel = new Label("Tổng tiền: " + formattedPrice);
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.RED);

        // Nút check status (Demo)
        Button checkStatusBtn = new Button("Kiểm tra trạng thái giao dịch");
        checkStatusBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20;");
        checkStatusBtn.setOnAction(e -> {
            // Demo check status logic
            showSuccessAlert("VietQR");
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(checkStatusBtn);

        box.getChildren().addAll(guide, qrView, statusLabel, totalLabel, buttonBox);
        return box;
    }

    private VBox createPayPalContent() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(30));
        box.setAlignment(Pos.CENTER);

        String formattedPrice = currencyFormatter.format(this.amount).replace("₫", "VND");
        Label amountLabel = new Label("Số tiền cần thanh toán: " + formattedPrice);
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label guideLabel = new Label("Hệ thống sẽ mở trình duyệt để bạn đăng nhập PayPal");
        guideLabel.setTextFill(Color.GRAY);

        Button payBtn = new Button("Thanh toán qua PayPal");
        payBtn.setStyle("-fx-background-color: #0070ba; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");

        payBtn.setOnAction(e -> {
            try {
                // [SOLID VIOLATION]: DIP - Lại vi phạm việc khởi tạo trực tiếp implementation class
                // [QUAN TRỌNG] Gọi Handler để xử lý logic PayPal
                // (Thay vì tự new PayPalSubsystem ở đây)
                String payUrl = handler.createPayPalOrder(this.amount);

                if (payUrl != null) {
                    // Mở trình duyệt
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(new URI(payUrl));
                    }

                    // Hiện thông báo chờ
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Đang thanh toán");
                    alert.setHeaderText("Vui lòng hoàn tất trên trình duyệt");
                    alert.setContentText("Sau khi thanh toán xong trên PayPal, hãy quay lại đây và bấm OK.");

                    var result = alert.showAndWait();
                    // [SOLID VIOLATION]: SRP - Logic hệ thống (Desktop.browse) bị nhúng vào View
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // User bấm OK -> coi như thành công (hoặc gọi handler.captureOrder nếu muốn kỹ hơn)
                        showSuccessAlert("PayPal");
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Không thể tạo đơn hàng PayPal. Vui lòng thử lại.");
                    alert.show();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Lỗi hệ thống: " + ex.getMessage());
                alert.show();
            }
        });

        box.getChildren().addAll(amountLabel, guideLabel, payBtn);
        return box;
    }

    private void showSuccessAlert(String method) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thanh toán thành công");
        alert.setHeaderText("Giao dịch hoàn tất!");
        alert.setContentText("Đã nhận khoản thanh toán " + currencyFormatter.format(this.amount) + " qua " + method + ".\nCảm ơn bạn!");
        alert.showAndWait();
        ((Stage) this.content.getScene().getWindow()).close();
    }
}