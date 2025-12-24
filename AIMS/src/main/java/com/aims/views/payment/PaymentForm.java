package com.aims.views.payment;

import com.aims.views.BaseForm;
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

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * [REFACTORED] PaymentForm - Tuân thủ SOLID
 * @author Pham Minh Dat
 */
public class PaymentForm extends BaseForm {

    private int amount;
    private String transactionContent;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    // Handler đóng vai trò Controller để xử lý logic, tách biệt khỏi UI (SRP)
    private PaymentScreenHandler handler;

    public PaymentForm(int amount, String content, PaymentScreenHandler handler) throws IOException {
        super();
        this.amount = amount;
        this.transactionContent = content;
        this.handler = handler;
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

        // Các Tab phương thức thanh toán
        Tab qrTab = new Tab("Thanh toán qua VietQR");
        qrTab.setContent(createVietQRContent());

        Tab creditTab = new Tab("Thẻ tín dụng (PayPal)");
        creditTab.setContent(createPayPalContent());

        paymentMethods.getTabs().addAll(qrTab, creditTab);

        Button cancelButton = new Button("Quay lại");
        cancelButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
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
            // DIP Fix: Không tạo mới Subsystem, gọi qua Handler
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

        String formattedPrice = currencyFormatter.format(this.amount);
        Label totalLabel = new Label("Tổng tiền: " + formattedPrice);
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.RED);

        box.getChildren().addAll(guide, qrView, statusLabel, totalLabel);
        return box;
    }

    private VBox createPayPalContent() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(30));
        box.setAlignment(Pos.CENTER);

        String formattedPrice = currencyFormatter.format(this.amount);
        Label amountLabel = new Label("Số tiền cần thanh toán: " + formattedPrice);
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label guideLabel = new Label("Hệ thống sẽ mở trình duyệt để bạn thanh toán Sandbox");
        guideLabel.setTextFill(Color.GRAY);

        Button payBtn = new Button("Thanh toán qua PayPal");
        payBtn.setStyle("-fx-background-color: #0070ba; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20; -fx-font-weight: bold;");

        // [SRP FIX]: View chỉ gửi yêu cầu, Handler lo thực thi và đợi kết quả
        payBtn.setOnAction(e -> {
            // Gọi hàm requestPayment (đã sửa lỗi cannot find symbol createPayPalOrder)
            handler.requestPayment(this.amount, this.transactionContent);

            // Sau khi gọi hàm này, Handler sẽ tự mở Browser và tự chuyển màn hình Success sau 10s
        });

        box.getChildren().addAll(amountLabel, guideLabel, payBtn);
        return box;
    }
}