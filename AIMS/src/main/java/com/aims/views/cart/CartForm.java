package com.aims.views.cart;

import com.aims.controller.PlaceOrderController;
import com.aims.controller.ViewCartController;
import com.aims.entity.cart.Cart;
import com.aims.entity.cart.CartMedia;
import com.aims.entity.order.Order;
import com.aims.utils.Configs;
import com.aims.utils.Utils;
import com.aims.views.popup.PopupForm;
import com.aims.views.shipping.DeliveryForm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Màn hình hiển thị giỏ hàng (Cart) dùng FXML: CartForm.fxml.
 */
public class CartForm {

    private static final Logger LOGGER = Utils.getLogger(CartForm.class.getName());

    private final Stage stage;
    private final ViewCartController viewCartController = new ViewCartController();
    private final PlaceOrderController placeOrderController = new PlaceOrderController();

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox itemsContainer;

    @FXML
    private Label labelSubtotal;

    @FXML
    private Label labelVAT;

    @FXML
    private Label labelAmount;

    @FXML
    private Label labelVatText;

    @FXML
    private Button placeOrderButton;

    @FXML
    private Button closeButton;

    public CartForm(Stage owner) {
        this.stage = new Stage();
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.setTitle("Cart");

        FXMLLoader loader = new FXMLLoader(
                CartForm.class.getResource("/com/aims/views/cart/CartForm.fxml")
        );
        loader.setController(this);
        try {
            BorderPane root = loader.load();
            Scene scene = new Scene(root, 700, 500);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load CartForm.fxml", e);
        }
    }

    /**
     * Hiển thị cửa sổ Cart và load dữ liệu.
     */
    public void show() {
        try {
            refreshCart();
        } catch (SQLException e) {
            e.printStackTrace();
            PopupForm.error("Error loading cart: " + e.getMessage());
        }
        stage.show();
    }

    @FXML
    private void initialize() {
        if (labelVatText != null) {
            labelVatText.setText("VAT (" + Configs.PERCENT_VAT + "%):");
        }

        if (placeOrderButton != null) {
            placeOrderButton.setOnAction(e -> {
                try {
                    handlePlaceOrder();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    PopupForm.error("Cannot place order: " + ex.getMessage());
                }
            });
        }

        if (closeButton != null) {
            closeButton.setOnAction(e -> stage.close());
        }
    }

    /**
     * Làm mới dữ liệu giỏ hàng: kiểm tra tồn kho, hiển thị lại list và tổng tiền.
     */
    public void refreshCart() throws SQLException {
        viewCartController.checkAvailabilityOfProduct();
        displayCartWithMediaAvailability();
    }

    private void displayCartWithMediaAvailability() {
        if (itemsContainer == null) return;

        itemsContainer.getChildren().clear();
        List<?> lstMedia = Cart.getCart().getListMedia();

        for (Object obj : lstMedia) {
            CartMedia cartMedia = (CartMedia) obj;
            MediaForm mediaForm = new MediaForm(cartMedia, this);
            itemsContainer.getChildren().add(mediaForm.getContent());
        }

        updateCartAmount();
    }

    /**
     * Cập nhật subtotal, VAT và tổng tiền.
     */
    public void updateCartAmount() {
        int subtotal = viewCartController.getCartSubtotal();
        int vat = (int) ((Configs.PERCENT_VAT / 100) * subtotal);
        int amount = subtotal + vat;

        LOGGER.info("Cart amount: " + amount);

        // Ở phần trước bạn đang dùng *1000 để đổi đơn vị, giữ nguyên hành vi đó
        if (labelSubtotal != null) {
            labelSubtotal.setText(Utils.getCurrencyFormat(subtotal * 1000));
        }
        if (labelVAT != null) {
            labelVAT.setText(Utils.getCurrencyFormat(vat * 1000));
        }
        if (labelAmount != null) {
            labelAmount.setText(Utils.getCurrencyFormat(amount * 1000));
        }
    }

    /**
     * Xử lý đặt hàng: kiểm tra giỏ hàng trống, kiểm tra tồn kho rồi mở form nhập thông tin giao hàng.
     */
    private void handlePlaceOrder() throws SQLException {
        if (Cart.getCart().getListMedia().isEmpty()) {
            PopupForm.error("You don't have anything to place");
            return;
        }

        try {
            // kiểm tra tồn kho
            placeOrderController.placeOrder();
            // tạo Order trong bộ nhớ
            Order order = placeOrderController.createOrder();
            // mở form nhập thông tin giao hàng
            DeliveryForm deliveryForm = new DeliveryForm(stage, order);
            deliveryForm.show();
        } catch (com.aims.exception.MediaNotAvailableException e) {
            PopupForm.error("Some media are not available, please check your cart.");
            refreshCart();
        }
    }
}
