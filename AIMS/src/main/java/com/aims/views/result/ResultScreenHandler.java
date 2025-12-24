package com.aims.views.result;

import com.aims.views.BaseScreenHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class ResultScreenHandler extends BaseScreenHandler {

    @FXML
    private Label resultLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Button okButton;

    public ResultScreenHandler(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
    }

    @FXML
    public void initialize() {
        // Khi bấm OK thì đóng cửa sổ kết quả
        okButton.setOnAction(e -> {
            // Nếu có màn hình Home thì quay về Home, không thì đóng luôn
            if (this.homeScreenHandler != null) {
                this.homeScreenHandler.show();
            } else {
                this.stage.close();
            }
        });
    }

    public void showResult(String msg) {
        // Kiểm tra xem label đã được nạp từ FXML chưa
        if (resultLabel != null) {
            resultLabel.setText(msg);
        }
        if (messageLabel != null) {
            messageLabel.setText("Cảm ơn bạn đã mua hàng!");
        }
    }
}