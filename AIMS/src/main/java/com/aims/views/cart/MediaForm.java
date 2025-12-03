package com.aims.views.cart;

import com.aims.entity.cart.Cart;
import com.aims.entity.cart.CartMedia;
import com.aims.utils.Utils;
import com.aims.views.popup.PopupForm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Một hàng trong giỏ hàng: hiển thị ảnh, tên, đơn giá*quantity, spinner số lượng và nút xóa.
 * Dùng FXML: MediaForm.fxml.
 */
public class MediaForm {

    private static final Logger LOGGER = Utils.getLogger(MediaForm.class.getName());

    private final CartMedia cartMedia;
    private final CartForm cartForm;

    @FXML
    private HBox root;

    @FXML
    private ImageView imageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label stockLabel;

    @FXML
    private Spinner<Integer> quantitySpinner;

    @FXML
    private Button deleteButton;

    public MediaForm(CartMedia cartMedia, CartForm cartForm) {
        this.cartMedia = cartMedia;
        this.cartForm = cartForm;

        FXMLLoader loader = new FXMLLoader(
                MediaForm.class.getResource("/com/aims/views/cart/MediaForm.fxml")
        );
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load MediaForm.fxml", e);
        }
    }

    public HBox getContent() {
        return root;
    }

    @FXML
    private void initialize() {
        // configure spinner
        quantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1,
                        Math.max(1, cartMedia.getMedia().getQuantity()),
                        cartMedia.getQuantity()
                )
        );

        setMediaInfo();
        setupActions();
    }

    private void setMediaInfo() {
        // image
        if (cartMedia.getMedia().getImageURL() != null) {
            try {
                File file = new File(cartMedia.getMedia().getImageURL());
                if (file.exists()) {
                    Image img = new Image(file.toURI().toString());
                    imageView.setImage(img);
                }
            } catch (Exception ignored) {
            }
        }

        titleLabel.setText(cartMedia.getMedia().getTitle());
        updatePriceLabel();
        stockLabel.setText("In stock: " + cartMedia.getMedia().getQuantity());
    }

    private void setupActions() {
        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            int numOfProd = newVal;
            int remainQuantity = cartMedia.getMedia().getQuantity();

            LOGGER.info("NumOfProd: " + numOfProd + " -- remainOfProd: " + remainQuantity);

            if (numOfProd > remainQuantity) {
                LOGGER.info("product " + cartMedia.getMedia().getTitle() + " only remains "
                        + remainQuantity + " (required " + numOfProd + ")");
                PopupForm.error("Only " + remainQuantity + " remain in stock");
                quantitySpinner.getValueFactory().setValue(remainQuantity);
                numOfProd = remainQuantity;
            }

            cartMedia.setQuantity(numOfProd);
            updatePriceLabel();
            cartForm.updateCartAmount();
        });

        deleteButton.setOnAction(e -> {
            try {
                Cart.getCart().removeCartMedia(cartMedia);
                cartForm.refreshCart();
                LOGGER.info("Deleted " + cartMedia.getMedia().getTitle() + " from the cart");
            } catch (Exception ex) {
                ex.printStackTrace();
                PopupForm.error("Cannot remove item from cart");
            }
        });
    }

    private void updatePriceLabel() {
        int total = cartMedia.getPrice() * cartMedia.getQuantity();
        priceLabel.setText(Utils.getCurrencyFormat(total * 1000));
    }
}
