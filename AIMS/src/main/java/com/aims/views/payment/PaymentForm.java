package com.aims.views.payment;

import com.aims.subsystem.IQRCodePayment;
import com.aims.subsystem.vietqr.VietQRSubsystem;
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

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentForm extends BaseForm {

    // Biến lưu số tiền và nội dung
    private int amount;
    private String transactionContent;

    // Định dạng tiền tệ
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

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
            statusLabel.setText("Đã tạo mã QR. Vui lòng quét.");
            statusLabel.setTextFill(Color.GREEN);

        } catch (Exception e) {
            statusLabel.setText("Lỗi: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }

        String formattedPrice = currencyFormatter.format(this.amount).replace("₫", "VND");
        Label totalLabel = new Label("Tổng tiền: " + formattedPrice);
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.RED);

        // --- NÚT GIẢ LẬP CALLBACK (Check Status) ---
        Button checkStatusBtn = new Button("Kiểm tra trạng thái giao dịch");
        checkStatusBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20;");

        checkStatusBtn.setOnAction(e -> {
            statusLabel.setText("Hệ thống đang kiểm tra giao dịch...");
            statusLabel.setTextFill(Color.ORANGE);
            checkStatusBtn.setDisable(true);

            // Giả lập độ trễ 2 giây
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));

            pause.setOnFinished(event -> {
                // Dùng Platform.runLater để tránh lỗi xung đột luồng
                Platform.runLater(() -> {
                    showSuccessAlert("VietQR (Callback Success)");
                });
            });
            pause.play();
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        // Chỉ thêm nút checkStatusBtn, bỏ confirmBtn
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

        // Hướng dẫn
        Label guideLabel = new Label("Hệ thống sẽ mở trình duyệt để bạn đăng nhập PayPal");
        guideLabel.setTextFill(Color.GRAY);

        // Nút thanh toán
        Button payBtn = new Button("Thanh toán qua PayPal");
        payBtn.setStyle("-fx-background-color: #0070ba; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");

        payBtn.setOnAction(e -> {
            try {
                // 1. Gọi Subsystem để lấy link
                IQRCodePayment paypalSystem = new com.aims.subsystem.paypal.PayPalSubsystem();
                String payUrl = paypalSystem.generatePayUrl(this.amount, this.transactionContent);

                // 2. Mở trình duyệt mặc định của máy tính
                java.awt.Desktop.getDesktop().browse(new java.net.URI(payUrl));

                // 3. Hiển thị thông báo chờ
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Đang thanh toán");
                alert.setHeaderText("Vui lòng hoàn tất trên trình duyệt");
                alert.setContentText("Sau khi thanh toán xong trên PayPal, hãy quay lại đây và bấm nút.");
                alert.showAndWait();

                // (Ở đây ta có thể giả lập callback thành công luôn sau khi user bấm OK)
                showSuccessAlert("PayPal (API Success)");

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Lỗi: " + ex.getMessage());
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
        // Đóng cửa sổ sau khi bấm OK
        ((Stage) this.content.getScene().getWindow()).close();
    }
}