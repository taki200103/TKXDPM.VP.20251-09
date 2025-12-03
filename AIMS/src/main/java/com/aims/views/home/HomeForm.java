package com.aims.views.home;

import com.aims.controller.HomeController;
import com.aims.entity.cart.Cart;
import com.aims.entity.cart.CartMedia;
import com.aims.entity.media.Media;
import com.aims.views.BaseForm;
import com.aims.views.payment.PaymentForm; // Import form thanh toán
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeForm extends BaseForm {

    private HomeController homeController;
    private ScrollPane scrollPane;
    private FlowPane productsPane;
    private static final int PRODUCTS_TO_DISPLAY = 20;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    public HomeForm() throws Exception {
        super();
        this.homeController = new HomeController();
        this.setBController(homeController);
        initializeUI();
        loadProducts();
    }

    private void initializeUI() {
        // Main container
        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #f5f5f5;");

        // Header
        HBox headerBox = createHeader();
        mainContainer.getChildren().add(headerBox);

        // Products container with scroll
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        productsPane = new FlowPane();
        productsPane.setHgap(20);
        productsPane.setVgap(20);
        productsPane.setPadding(new Insets(20));
        productsPane.setAlignment(Pos.CENTER);

        scrollPane.setContent(productsPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        mainContainer.getChildren().add(scrollPane);

        this.content.getChildren().add(mainContainer);
        AnchorPane.setTopAnchor(mainContainer, 0.0);
        AnchorPane.setBottomAnchor(mainContainer, 0.0);
        AnchorPane.setLeftAnchor(mainContainer, 0.0);
        AnchorPane.setRightAnchor(mainContainer, 0.0);
    }

    private HBox createHeader() {
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(15, 20, 15, 20));
        headerBox.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 10;");

        Label titleLabel = new Label("AIMS - Home");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        // Cart button
        Button cartButton = new Button("View Cart (" + Cart.getCart().getCartSize() + ")");
        cartButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10 20; -fx-background-radius: 5;");

        // SỬA ĐOẠN NÀY ĐỂ KẾT NỐI VỚI MÀN HÌNH THANH TOÁN
        cartButton.setOnAction(e -> {
            try {
                // Mở màn hình thanh toán giả lập
                PaymentForm paymentForm = new PaymentForm();

                Stage stage = new Stage();
                stage.setTitle("Thanh toán - AIMS");
                stage.setScene(new Scene(paymentForm.getContent()));
                stage.setWidth(800);
                stage.setHeight(600);
                stage.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerBox.getChildren().addAll(titleLabel, spacer, cartButton);
        return headerBox;
    }

    @SuppressWarnings("unchecked")
    private void loadProducts() {
        try {
            List<Media> allMedia = (List<Media>) homeController.getAllMedia();
            int displayCount = Math.min(PRODUCTS_TO_DISPLAY, allMedia.size());

            for (int i = 0; i < displayCount; i++) {
                Media media = allMedia.get(i);
                VBox productCard = createProductCard(media);
                productsPane.getChildren().add(productCard);
            }

            if (allMedia.size() > PRODUCTS_TO_DISPLAY) {
                Label infoLabel = new Label("Showing " + PRODUCTS_TO_DISPLAY + " of " + allMedia.size() + " products");
                infoLabel.setFont(Font.font("Arial", 12));
                infoLabel.setTextFill(Color.GRAY);
                infoLabel.setPadding(new Insets(10));
                productsPane.getChildren().add(infoLabel);
            }

        } catch (Exception e) {
            showError("Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createProductCard(Media media) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(200);
        card.setMaxWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Product Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(170);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");

        if (media.getImageURL() != null && !media.getImageURL().isEmpty()) {
            try {
                File imageFile = new File(media.getImageURL());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                } else {
                    imageView.setStyle("-fx-background-color: #bdc3c7; -fx-background-radius: 5;");
                }
            } catch (Exception e) {
                imageView.setStyle("-fx-background-color: #bdc3c7; -fx-background-radius: 5;");
            }
        } else {
            imageView.setStyle("-fx-background-color: #bdc3c7; -fx-background-radius: 5;");
        }

        Label titleLabel = new Label(media.getTitle());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(170);

        Label categoryLabel = new Label(media.getCategory());
        categoryLabel.setFont(Font.font("Arial", 11));
        categoryLabel.setTextFill(Color.GRAY);

        Label priceLabel = new Label(formatPrice(media.getPrice()));
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        priceLabel.setTextFill(Color.web("#e74c3c"));

        Label stockLabel = new Label("Stock: " + media.getQuantity());
        stockLabel.setFont(Font.font("Arial", 10));
        if (media.getQuantity() > 0) {
            stockLabel.setTextFill(Color.web("#27ae60"));
        } else {
            stockLabel.setTextFill(Color.web("#e74c3c"));
        }

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 5;");
        addToCartButton.setMaxWidth(Double.MAX_VALUE);
        addToCartButton.setDisable(media.getQuantity() == 0);

        addToCartButton.setOnAction(e -> {
            CartMedia existingItem = Cart.getCart().checkMediaInCart(media.getId());
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
            } else {
                Cart.getCart().addCartMedia(new CartMedia(media, 1, media.getPrice()));
            }
            updateCartButton();

            // Thông báo đơn giản khi thêm vào giỏ
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Added " + media.getTitle() + " to cart!");
            alert.showAndWait();
        });

        card.getChildren().addAll(imageView, titleLabel, categoryLabel, priceLabel, stockLabel, addToCartButton);
        card.setAlignment(Pos.CENTER);

        return card;
    }

    private void updateCartButton() {
        // Tạm thời bỏ qua
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