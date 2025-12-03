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

import java.io.File;
import java.io.IOException;

public class PaymentForm extends BaseForm {

    public PaymentForm() throws IOException {
        super();
        initializeUI();
    }

    private void initializeUI() {
        // Container chính
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setStyle("-fx-background-color: white;");

        // Tiêu đề
        Label titleLabel = new Label("Thanh toán đơn hàng");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        // TabPane để chuyển đổi giữa VietQR và PayPal
        TabPane paymentMethods = new TabPane();
        paymentMethods.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        paymentMethods.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1px;");

        // --- TAB 1: VIETQR (Dùng Subsystem thật) ---
        Tab qrTab = new Tab("Thanh toán qua VietQR");
        qrTab.setContent(createVietQRContent());

        // --- TAB 2: PAYPAL (Giả lập giao diện) ---
        Tab creditTab = new Tab("Thẻ tín dụng (PayPal)");
        creditTab.setContent(createPayPalContent());

        paymentMethods.getTabs().addAll(qrTab, creditTab);

        // Nút Hủy bỏ
        Button cancelButton = new Button("Quay lại");
        cancelButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px;");
        cancelButton.setOnAction(e -> {
            // Đóng cửa sổ
            ((Stage) this.content.getScene().getWindow()).close();
        });

        mainContainer.getChildren().addAll(titleLabel, paymentMethods, cancelButton);

        this.content.getChildren().add(mainContainer);
        AnchorPane.setTopAnchor(mainContainer, 0.0);
        AnchorPane.setBottomAnchor(mainContainer, 0.0);
        AnchorPane.setLeftAnchor(mainContainer, 0.0);
        AnchorPane.setRightAnchor(mainContainer, 0.0);
    }

    // --- PHẦN 1: GIAO DIỆN VIETQR (GỌI SUBSYSTEM) ---
    private VBox createVietQRContent() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        Label guide = new Label("Quét mã để thanh toán (VietQR API):");
        guide.setFont(Font.font("Arial", 14));

        // Khung hiển thị ảnh QR
        ImageView qrView = new ImageView();
        qrView.setFitWidth(300);
        qrView.setFitHeight(300);
        qrView.setPreserveRatio(true);
        qrView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1px;");

        Label statusLabel = new Label("Đang tải mã QR...");
        statusLabel.setTextFill(Color.BLUE);

        // --- BẮT ĐẦU GỌI SUBSYSTEM ---
        try {
            // 1. Khởi tạo Subsystem thông qua Interface
            IQRCodePayment paymentSubsystem = new VietQRSubsystem();

            // 2. Gọi hàm generatePayUrl (Số tiền giả định 150k)
            String qrUrl = paymentSubsystem.generatePayUrl(150000, "Thanh toan don hang AIMS");

            // 3. Hiển thị ảnh từ URL trả về (backgroundLoading = true)
            Image image = new Image(qrUrl, true);
            qrView.setImage(image);

            // Xóa chữ loading khi đã có link
            statusLabel.setText("");

        } catch (Exception e) {
            statusLabel.setText("Lỗi: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
        // --- KẾT THÚC GỌI SUBSYSTEM ---

        Label totalLabel = new Label("Tổng tiền: 150.000 VND");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.RED);

        Button confirmBtn = new Button("Đã thanh toán xong");
        confirmBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px;");
        confirmBtn.setOnAction(e -> showSuccessAlert("VietQR"));

        box.getChildren().addAll(guide, qrView, statusLabel, totalLabel, confirmBtn);
        return box;
    }

    // --- PHẦN 2: GIAO DIỆN PAYPAL (MOCK UI) ---
    private VBox createPayPalContent() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(30));
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(400);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Các trường nhập liệu giả
        grid.add(new Label("Chủ thẻ:"), 0, 0);
        TextField nameField = new TextField();
        nameField.setPromptText("NGUYEN VAN A");
        grid.add(nameField, 1, 0);

        grid.add(new Label("Số thẻ:"), 0, 1);
        TextField cardField = new TextField();
        cardField.setPromptText("xxxx-xxxx-xxxx-xxxx");
        grid.add(cardField, 1, 1);

        grid.add(new Label("Ngày hết hạn:"), 0, 2);
        TextField dateField = new TextField();
        dateField.setPromptText("MM/YY");
        grid.add(dateField, 1, 2);

        grid.add(new Label("Mã CVV:"), 0, 3);
        TextField cvvField = new TextField();
        cvvField.setPromptText("***");
        grid.add(cvvField, 1, 3);

        Button payBtn = new Button("Thanh toán ngay");
        payBtn.setStyle("-fx-background-color: #0070ba; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");
        payBtn.setOnAction(e -> showSuccessAlert("PayPal (Credit Card)"));

        box.getChildren().addAll(new Label("Nhập thông tin thẻ tín dụng:"), grid, payBtn);
        return box;
    }

    private void showSuccessAlert(String method) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thanh toán thành công");
        alert.setHeaderText("Giao dịch hoàn tất!");
        alert.setContentText("Bạn đã thanh toán thành công qua phương thức: " + method + ".\nHóa đơn điện tử đã được gửi về email.");
        alert.showAndWait();
    }
}