package com.aims.views.shipping;

import com.aims.entity.deliveryInfo.DeliveryInfo;
import com.aims.entity.order.Order;
import com.aims.utils.Utils;
import com.aims.views.popup.PopupForm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Form nhập thông tin giao hàng khi người dùng bấm Place Order từ giỏ hàng.
 */
public class DeliveryForm {

    private static final Logger LOGGER = Utils.getLogger(DeliveryForm.class.getName());

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label screenTitle;

    @FXML
    private TextField name;

    @FXML
    private TextField phone;

    @FXML
    private TextField address;

    @FXML
    private TextField email;

    @FXML
    private TextField instructions;

    @FXML
    private ComboBox<String> province;

    @FXML
    private Button btnConfirmDelivery;

    @FXML
    private Button cancelButton;

    private final Stage stage;
    private final Order order;

    public DeliveryForm(Stage owner, Order order) {
        this.order = order;
        this.stage = new Stage();
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.setTitle("Delivery Information");

        FXMLLoader loader = new FXMLLoader(
                DeliveryForm.class.getResource("/com/aims/views/shipping/DeliveryForm.fxml")
        );
        loader.setController(this);
        try {
            BorderPane root = loader.load();
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load DeliveryForm.fxml", e);
        }
    }

    public void show() {
        stage.show();
    }

    @FXML
    private void initialize() {
        if (screenTitle != null) {
            screenTitle.setText("Delivery Information");
        }

        if (province != null) {
            List<String> provinces = Arrays.asList(
                    "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ"
            );
            province.getItems().setAll(provinces);
        }

        if (btnConfirmDelivery != null) {
            btnConfirmDelivery.setOnAction(e -> {
                try {
                    submitDeliveryInfo();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    PopupForm.error("Cannot submit delivery info: " + ex.getMessage());
                }
            });
        }

        if (cancelButton != null) {
            cancelButton.setOnAction(e -> stage.close());
        }
    }

    private void submitDeliveryInfo() throws SQLException {
        // tạo / cập nhật DeliveryInfo cho order
        if (order == null) {
            PopupForm.error("Order is not initialized.");
            return;
        }

        DeliveryInfo deliveryInfo = order.getDeliveryInfo();
        if (deliveryInfo == null) {
            deliveryInfo = new DeliveryInfo();
            order.setDeliveryInfo(deliveryInfo);
        }

        try {
            deliveryInfo.setRecipientName(name.getText());
            deliveryInfo.setPhoneNumber(phone.getText());
            deliveryInfo.setDeliveryAddress(address.getText());
            deliveryInfo.setCity(province.getValue());
            deliveryInfo.setEmail(email.getText());
            String baseInstr = instructions.getText() != null ? instructions.getText().trim() : "";
            deliveryInfo.setInstructions(baseInstr);
        } catch (com.aims.exception.InvalidDeliveryInfoException e) {
            PopupForm.error(e.getMessage());
            return;
        }

        PopupForm.success("Delivery information saved successfully!");
        stage.close();
    }
}
