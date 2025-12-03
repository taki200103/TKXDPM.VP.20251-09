package com.aims.views.home;

import com.aims.controller.HomeController;
import com.aims.entity.cart.Cart;
import com.aims.entity.media.Media;
import com.aims.views.cart.CartForm;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * JavaFX controller cho màn hình Home, dùng cùng với Home.fxml.
 */
public class HomeForm {

    // Số sản phẩm tối đa hiển thị trên trang home
    private static final int PRODUCTS_TO_DISPLAY = 20;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox mainContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane productsPane;

    @FXML
    private Button cartButton;

    @FXML
    private Label titleLabel;

    @FXML
    private Label cartSummaryLabel;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private ComboBox<String> sortCombo;

    @FXML
    private ComboBox<String> categoryCombo;

    private HomeController homeController;
    private List<Media> allMedia;
    private final NumberFormat currencyFormatter =
            NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    @FXML
    private void initialize() {
        // Khởi tạo controller domain
        this.homeController = new HomeController();

        // Cấu hình header (title, cart button)
        setupHeader();

        // Cấu hình scroll pane nếu cần thêm thuộc tính
        if (scrollPane != null) {
            scrollPane.setFitToWidth(true);
        }

        // Load dữ liệu sản phẩm
        loadProducts();
    }

    private void setupHeader() {
        if (titleLabel != null) {
            titleLabel.setFont(Font.font("Arial", 24));
        }

        if (cartButton != null) {
            updateCartButtonLabel();
            cartButton.setOnAction(e -> {
                // Mở màn hình Cart
                javafx.stage.Stage owner = (javafx.stage.Stage) rootPane.getScene().getWindow();
                CartForm cartForm = new CartForm(owner);
                cartForm.show();
            });
        }

        // Thiết lập combobox và search
        setupFilters();
    }

    @SuppressWarnings("unchecked")
    private void loadProducts() {
        try {
            this.allMedia = (List<Media>) homeController.getAllMedia();
            applyFiltersAndRender();

        } catch (SQLException e) {
            showError("Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật text nút giỏ hàng (số lượng item).
     */
    public void updateCartButtonLabel() {
        if (cartButton != null) {
            cartButton.setText("View Cart (" + Cart.getCart().getCartSize() + ")");
        }
        if (cartSummaryLabel != null) {
            cartSummaryLabel.setText(Cart.getCart().getCartSize() + " media");
        }
    }

    private void setupFilters() {
        if (sortCombo != null) {
            sortCombo.getItems().setAll(
                    "Default",
                    "Title A-Z",
                    "Price Low-High",
                    "Price High-Low"
            );
            sortCombo.getSelectionModel().selectFirst();
            sortCombo.setOnAction(e -> applyFiltersAndRender());
        }

        if (categoryCombo != null) {
            categoryCombo.getItems().setAll(
                    "All",
                    "Book",
                    "CD",
                    "DVD"
            );
            categoryCombo.getSelectionModel().selectFirst();
            categoryCombo.setOnAction(e -> applyFiltersAndRender());
        }

        if (searchButton != null && searchField != null) {
            searchButton.setOnAction(e -> applyFiltersAndRender());
            searchField.setOnAction(e -> applyFiltersAndRender());
        }
    }

    /**
     * Áp dụng search, sort, category filter lên allMedia và hiển thị lại.
     */
    private void applyFiltersAndRender() {
        if (allMedia == null || productsPane == null) return;

        String keyword = (searchField != null && searchField.getText() != null)
                ? searchField.getText().trim().toLowerCase()
                : "";
        String category = (categoryCombo != null && categoryCombo.getValue() != null)
                ? categoryCombo.getValue()
                : "All";
        String sortBy = (sortCombo != null && sortCombo.getValue() != null)
                ? sortCombo.getValue()
                : "Default";

        // Lọc theo category + search
        List<Media> filtered = allMedia.stream()
                .filter(m -> "All".equalsIgnoreCase(category) || m.getCategory().equalsIgnoreCase(category))
                .filter(m -> keyword.isEmpty() || m.getTitle().toLowerCase().contains(keyword))
                .toList();

        // Sắp xếp
        filtered = switch (sortBy) {
            case "Title A-Z" ->
                    filtered.stream().sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle())).toList();
            case "Price Low-High" ->
                    filtered.stream().sorted((a, b) -> Integer.compare(a.getPrice(), b.getPrice())).toList();
            case "Price High-Low" ->
                    filtered.stream().sorted((a, b) -> Integer.compare(b.getPrice(), a.getPrice())).toList();
            default -> filtered;
        };

        // Lấy ngẫu nhiên tối đa PRODUCTS_TO_DISPLAY sản phẩm
        List<Media> randomized = new ArrayList<>(filtered);
        Collections.shuffle(randomized);
        if (randomized.size() > PRODUCTS_TO_DISPLAY) {
            randomized = randomized.subList(0, PRODUCTS_TO_DISPLAY);
        }

        // Render lại
        productsPane.getChildren().clear();
        for (Media media : randomized) {
            MediaForm mediaForm = new MediaForm(media, this);
            productsPane.getChildren().add(mediaForm.getContent());
        }
    }

    private String formatPrice(int price) {
        return currencyFormatter.format(price).replace("₫", "VND");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

