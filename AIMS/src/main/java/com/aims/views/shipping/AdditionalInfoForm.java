package com.aims.views.shipping;

import com.aims.entity.deliveryInfo.DeliveryInfo;
import com.aims.entity.order.Order;
import com.aims.views.popup.PopupForm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Form nhập thêm thông tin cho đơn hàng rush (thời gian giao, hướng dẫn giao hàng).
 */
public class AdditionalInfoForm {

    @FXML
    private VBox root;

    @FXML
    private TextField deliveryTime;

    @FXML
    private TextField deliveryInstructions;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final Stage stage;
    private final Order order;
    private boolean saved = false;

    public AdditionalInfoForm(Stage owner, Order order) {
        this.order = order;
        this.stage = new Stage();
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.setTitle("Additional Information");

        FXMLLoader loader = new FXMLLoader(
                AdditionalInfoForm.class.getResource("/com/aims/views/shipping/AdditionalInfoForm.fxml")
        );
        loader.setController(this);
        try {
            VBox rootNode = loader.load();
            Scene scene = new Scene(rootNode);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load AdditionalInfoForm.fxml", e);
        }
    }

    public boolean showAndWait() {
        stage.showAndWait();
        return saved;
    }

    @FXML
    private void initialize() {
        if (saveButton != null) {
            saveButton.setOnAction(e -> {
                try {
                    submitAdditionalInfo();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    PopupForm.error("Cannot save additional info: " + ex.getMessage());
                }
            });
        }

        if (cancelButton != null) {
            cancelButton.setOnAction(e -> {
                saved = false;
                stage.close();
            });
        }
    }

    private void submitAdditionalInfo() throws IOException {
        String time = deliveryTime.getText().trim();

        if (!time.matches("\\d{2}:\\d{2}")) {
            PopupForm.error("Invalid time format. Please enter time in hh:mm format.");
            return;
        }

        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
            PopupForm.error("Invalid time. Hours must be between 00-23 and minutes between 00-59.");
            return;
        }

        if (order != null) {
            DeliveryInfo info = order.getDeliveryInfo();
            if (info == null) {
                info = new DeliveryInfo();
                order.setDeliveryInfo(info);
            }
            String instructions = deliveryInstructions.getText().trim();
            // ghép thời gian vào instructions cho đơn giản
            String fullInstructions = "Preferred time: " + time +
                    (instructions.isEmpty() ? "" : (" | " + instructions));
            info.setInstructions(fullInstructions);
        }

        PopupForm.success("Additional information has been saved successfully!");
        saved = true;
        stage.close();
    }
}
