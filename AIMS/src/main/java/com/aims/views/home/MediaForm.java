package com.aims.views.home;

import com.aims.entity.cart.Cart;
import com.aims.entity.cart.CartMedia;
import com.aims.entity.media.Book;
import com.aims.entity.media.CD;
import com.aims.entity.media.DVD;
import com.aims.entity.media.Media;
import com.aims.exception.MediaNotAvailableException;
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
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Component hiển thị thông tin một media (sản phẩm) và cho phép thêm vào giỏ hàng.
 * Được thiết kế để có thể dùng trong HomeForm (danh sách sản phẩm).
 */
public class MediaForm {

    private static final Logger LOGGER = Utils.getLogger(MediaForm.class.getName());

    private final Media media;
    private final HomeForm home;

    @FXML
    private VBox root;

    @FXML
    private ImageView mediaImage;

    @FXML
    private Label mediaTitle;

    @FXML
    private Label mediaPrice;

    @FXML
    private Label mediaAvail;

    @FXML
    private Spinner<Integer> quantitySpinner;

    @FXML
    private Button addToCartBtn;

    public MediaForm(Media media, HomeForm home) {
        this.media = media;
        this.home = home;

        FXMLLoader loader = new FXMLLoader(
                MediaForm.class.getResource("/com/aims/views/home/MediaForm.fxml")
        );
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load MediaForm.fxml", e);
        }
    }

    /**
     * Trả về node gốc để gắn vào layout bên ngoài (ví dụ: FlowPane trong HomeForm).
     */
    public VBox getContent() {
        return root;
    }

    public Media getMedia() {
        return media;
    }

    @FXML
    private void initialize() {
        // Cấu hình spinner
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1));

        // Thiết lập thông tin media
        setMediaInfo();

        // Thiết lập hành vi thêm vào giỏ
        setupAddToCartHandler();

        // Click vào card sẽ mở popup chi tiết
        root.setOnMouseClicked(event -> showDetailDialog());
    }

    private void setMediaInfo() {
        // Ảnh
        if (media.getImageURL() != null && !media.getImageURL().isEmpty()) {
            try {
                File imageFile = new File(media.getImageURL());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    mediaImage.setImage(image);
                } else {
                    mediaImage.setStyle("-fx-background-color: #bdc3c7; -fx-background-radius: 5;");
                }
            } catch (Exception e) {
                mediaImage.setStyle("-fx-background-color: #bdc3c7; -fx-background-radius: 5;");
            }
        } else {
            mediaImage.setStyle("-fx-background-color: #bdc3c7; -fx-background-radius: 5;");
        }

        // Các thông tin khác
        mediaTitle.setText(media.getTitle());
        mediaPrice.setText(Utils.getCurrencyFormat(media.getPrice()));
        mediaAvail.setText("Available: " + media.getQuantity());

        // Nếu hết hàng thì disable nút và spinner
        boolean outOfStock = media.getQuantity() <= 0;
        addToCartBtn.setDisable(outOfStock);
        quantitySpinner.setDisable(outOfStock);
    }

    private void setupAddToCartHandler() {
        addToCartBtn.setOnAction(event -> {
            try {
                int amount = quantitySpinner.getValue() != null ? quantitySpinner.getValue() : 0;
                if (amount == 0) return;

                if (amount > media.getQuantity()) {
                    throw new MediaNotAvailableException();
                }

                Cart cart = Cart.getCart();
                CartMedia mediaInCart = cart.checkMediaInCart(media.getId());

                if (mediaInCart != null) {
                    // kiểm tra tổng số lượng sau khi cộng thêm
                    if (mediaInCart.getQuantity() + amount > media.getQuantity()) {
                        throw new MediaNotAvailableException(mediaInCart.getQuantity());
                    }
                    mediaInCart.setQuantity(mediaInCart.getQuantity() + amount);
                } else {
                    CartMedia cartMedia = new CartMedia(media, amount, media.getPrice());
                    cart.addCartMedia(cartMedia);
                    LOGGER.info("Added " + cartMedia.getQuantity() + " " + media.getTitle() + " to cart");
                }

                PopupForm.success("The media " + media.getTitle() + " added to Cart");

            } catch (MediaNotAvailableException exp) {
                String message = "Not enough stock:\nRequired: " + quantitySpinner.getValue() +
                        "\nAvailable: " + media.getQuantity() +
                        (exp.getQuantityInCart() > 0 ? "\nIn cart: " + exp.getQuantityInCart() : "");
                LOGGER.severe(message);
                PopupForm.error(message);

            } catch (Exception exp) {
                LOGGER.severe("Cannot add media to cart: " + exp.getMessage());
                exp.printStackTrace();
            }
        });
    }

    /**
     * Hiển thị thông tin chi tiết media với các field riêng cho Book / CD / DVD.
     */
    private void showDetailDialog() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Media Details");
        alert.setHeaderText(media.getTitle());

        StringBuilder content = new StringBuilder();
        content.append("Category: ").append(media.getCategory()).append("\n");
        content.append("Price: ").append(Utils.getCurrencyFormat(media.getPrice())).append("\n");
        content.append("Available: ").append(media.getQuantity()).append("\n");

        if (media.getDescription() != null) {
            content.append("Description: ").append(media.getDescription()).append("\n");
        }

        // Thông tin riêng theo loại
        if (media instanceof Book book) {
            content.append("\n-- Book details --\n");
            content.append("Author: ").append(book.getAuthor()).append("\n");
            content.append("Cover: ").append(book.getCoverType()).append("\n");
            content.append("Category: ").append(book.getBookCategory()).append("\n");
            content.append("Publisher: ").append(book.getPublisher()).append("\n");
            content.append("Language: ").append(book.getLanguage()).append("\n");
            content.append("Pages: ").append(book.getNumOfPages()).append("\n");
            content.append("Publish date: ").append(book.getPublishDate()).append("\n");
        } else if (media instanceof CD cd) {
            content.append("\n-- CD details --\n");
            content.append("Artist: ").append(cd.getArtist()).append("\n");
            content.append("Record label: ").append(cd.getRecordLabel()).append("\n");
            content.append("Music type: ").append(cd.getMusicType()).append("\n");
            content.append("Release date: ").append(cd.getReleasedDate()).append("\n");
        } else if (media instanceof DVD dvd) {
            content.append("\n-- DVD details --\n");
            content.append("Director: ").append(dvd.getDirector()).append("\n");
            content.append("Studio: ").append(dvd.getStudio()).append("\n");
            content.append("Runtime: ").append(dvd.getRuntime()).append(" minutes\n");
            content.append("Subtitles: ").append(dvd.getSubtitles()).append("\n");
            content.append("Disc type: ").append(dvd.getDiscType()).append("\n");
            content.append("Release date: ").append(dvd.getReleaseDate()).append("\n");
        }

        alert.setContentText(content.toString());
        alert.showAndWait();
    }
}
