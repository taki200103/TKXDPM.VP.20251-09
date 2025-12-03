package com.aims.views.payment;

import com.aims.subsystem.IQRCodePayment;
import com.aims.subsystem.vietqr.VietQRSubsystem;
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

public class PaymentForm extends BaseForm {

    // Thêm biến để lưu số tiền
    private int amount;
    private String transactionContent;

    // Định dạng tiền tệ (Ví dụ: 150.000 VND)
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    // SỬA CONSTRUCTOR: Nhận thêm tham số amount và content
    public PaymentForm(int amount, String content) throws IOException {
        super();
        this.amount = amount;
        this.transactionContent = content;
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
            IQRCodePayment paymentSubsystem = new VietQRSubsystem();

            String qrUrl = paymentSubsystem.generatePayUrl(this.amount, this.transactionContent);

            Image image = new Image(qrUrl, true);
            qrView.setImage(image);
            statusLabel.setText("");

        } catch (Exception e) {
            statusLabel.setText("Lỗi: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }

        // SỬA: Hiển thị đúng số tiền
        String formattedPrice = currencyFormatter.format(this.amount).replace("₫", "VND");
        Label totalLabel = new Label("Tổng tiền: " + formattedPrice);
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.RED);

        Button confirmBtn = new Button("Đã thanh toán xong");
        confirmBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px;");
        confirmBtn.setOnAction(e -> showSuccessAlert("VietQR"));

        box.getChildren().addAll(guide, qrView, statusLabel, totalLabel, confirmBtn);
        return box;
    }

    private VBox createPayPalContent() {
        // (Giữ nguyên phần này như cũ, chỉ cần sửa logic hiển thị tiền nếu muốn)
        VBox box = new VBox(15);
        box.setPadding(new Insets(30));
        box.setAlignment(Pos.CENTER);

        // Hiển thị tổng tiền động ở tab Paypal luôn
        String formattedPrice = currencyFormatter.format(this.amount).replace("₫", "VND");
        Label amountLabel = new Label("Số tiền cần thanh toán: " + formattedPrice);
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button payBtn = new Button("Thanh toán ngay");
        payBtn.setStyle("-fx-background-color: #0070ba; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");
        payBtn.setOnAction(e -> showSuccessAlert("PayPal (Credit Card)"));

        box.getChildren().addAll(amountLabel, payBtn);
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