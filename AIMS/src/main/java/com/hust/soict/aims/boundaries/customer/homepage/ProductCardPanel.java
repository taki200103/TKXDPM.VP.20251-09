package com.hust.soict.aims.boundaries.customer.homepage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.components.RoundedPanel;
import com.hust.soict.aims.components.RoundedButton;
import com.hust.soict.aims.utils.ImageUtils;
import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductCardPanel extends RoundedPanel {
    private final Product product;
    private final Component parentComponent;

    // Callbacks
    private ActionListener onAddToCart;
    private ActionListener onViewInfo;

    /**
     * Constructor
     * 
     * @param product Product to display
     * @param cart    CartController for add to cart action
     * @param parent  Parent component for dialog positioning
     */
    public ProductCardPanel(Product product, Component parent) {
        super(12, true); // Rounded corners with shadow
        this.product = product;
        this.parentComponent = parent;

        setBackground(BACKGROUND_WHITE);
        setBorder(BorderFactory.createEmptyBorder(SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM));

        setupUI();
    }

    /**
     * Setup the UI components and layout
     */
    private void setupUI() {
        setLayout(new BorderLayout(SPACING_SMALL, SPACING_SMALL));

        // Main content panel: Image left, Info right
        JPanel contentPanel = new JPanel(new BorderLayout(SPACING_MEDIUM, 0));
        contentPanel.setOpaque(false);

        // Left: Product image (square)
        JPanel imagePanel = createImagePanel();
        contentPanel.add(imagePanel, BorderLayout.WEST);

        // Right: Product info
        JPanel infoPanel = createInfoPanel();
        contentPanel.add(infoPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Bottom: Buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Create product image panel (square, left side)
     */
    private JPanel createImagePanel() {
        JPanel imagePanel = new RoundedPanel(8, false);
        imagePanel.setBackground(BACKGROUND_LIGHT);
        int imageSize = 120; // Square image
        imagePanel.setPreferredSize(new Dimension(imageSize, imageSize));
        imagePanel.setMinimumSize(new Dimension(imageSize, imageSize));
        imagePanel.setMaximumSize(new Dimension(imageSize, imageSize));
        imagePanel
                .setBorder(BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_SMALL, SPACING_SMALL, SPACING_SMALL));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Get image path (use ImageUtils to get path from product ID)
        String imagePath = product.getImagePath();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = ImageUtils.getProductImagePath(product.getId());
        } else {
            // Verify the path exists, if not try to get from ID
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                imagePath = ImageUtils.getProductImagePath(product.getId());
            }
        }

        // Try to load image if path exists
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage();
                // Scale image to fit square while maintaining aspect ratio
                int maxSize = 100; // Leave some padding
                int imgWidth = icon.getIconWidth();
                int imgHeight = icon.getIconHeight();

                double scale = Math.min((double) maxSize / imgWidth, (double) maxSize / imgHeight);
                int scaledWidth = (int) (imgWidth * scale);
                int scaledHeight = (int) (imgHeight * scale);

                Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                // Placeholder for missing image
                imageLabel.setText(
                        "<html><center><div style='color: #999; font-size: 11px;'>No<br>Image</div></center></html>");
                imageLabel.setFont(FONT_SMALL);
                imageLabel.setForeground(TEXT_SECONDARY);
            }
        } else {
            // Placeholder for no image path
            imageLabel.setText(
                    "<html><center><div style='color: #999; font-size: 11px;'>No<br>Image</div></center></html>");
            imageLabel.setFont(FONT_SMALL);
            imageLabel.setForeground(TEXT_SECONDARY);
        }

        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        return imagePanel;
    }

    /**
     * Create product info panel (right side)
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Product title
        JLabel titleLabel = new JLabel("<html><div style='text-align: left; width: 100%;'>" +
                product.getTitle() + "</div></html>");
        titleLabel.setFont(FONT_PRODUCT_NAME);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(SPACING_SMALL));

        // Price with VND format (with thousand separators)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        String priceText = df.format((long) product.getCurrentPrice()) + " VND";

        JLabel priceLabel = new JLabel(priceText);
        priceLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_PRODUCT_NAME));
        priceLabel.setForeground(PRIMARY_COLOR);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(SPACING_XSMALL));

        // Weight info
        JLabel weightLabel = new JLabel(String.format("Weight: %.2f kg", product.getWeight()));
        weightLabel.setFont(FONT_SMALL);
        weightLabel.setForeground(TEXT_SECONDARY);
        weightLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(weightLabel);
        infoPanel.add(Box.createVerticalStrut(SPACING_XSMALL));

        // Stock quantity info
        JLabel stockLabel = new JLabel(String.format("Available: %d", product.getQuantity()));
        stockLabel.setFont(FONT_SMALL);
        stockLabel.setForeground(TEXT_SECONDARY);
        stockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(stockLabel);

        // Add flexible space to push content to top
        infoPanel.add(Box.createVerticalGlue());

        return infoPanel;
    }

    /**
     * Create button panel (bottom)
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, SPACING_SMALL, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_SMALL, 0, 0, 0));

        RoundedButton addButton = createAddButton();
        RoundedButton infoButton = createInfoButton();

        buttonPanel.add(addButton);
        buttonPanel.add(infoButton);

        return buttonPanel;
    }

    /**
     * Create "Add to Cart" button
     */
    private RoundedButton createAddButton() {
        RoundedButton addButton = new RoundedButton("Add to Cart", 8);
        addButton.setFont(FONT_BUTTON);
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(TEXT_ON_PRIMARY);
        addButton.setCursor(CURSOR_HAND);

        addButton.addActionListener(e -> {
            CartController.getInstance().addProduct(product, 1);

            // Show confirmation message
            JOptionPane.showMessageDialog(parentComponent,
                    "Added to cart: " + product.getTitle(),
                    "Cart",
                    JOptionPane.INFORMATION_MESSAGE);

            // Trigger callback if set
            if (onAddToCart != null) {
                onAddToCart.actionPerformed(e);
            }
        });

        return addButton;
    }

    /**
     * Create "Info" button
     */
    private RoundedButton createInfoButton() {
        RoundedButton infoButton = new RoundedButton("Details", 8);
        infoButton.setFont(FONT_BUTTON);
        infoButton.setBackground(BACKGROUND_GRAY);
        infoButton.setForeground(TEXT_PRIMARY);
        infoButton.setCursor(CURSOR_HAND);

        infoButton.addActionListener(e -> {
            // Trigger callback if set
            if (onViewInfo != null) {
                onViewInfo.actionPerformed(e);
            }
        });

        return infoButton;
    }

    /**
     * Set callback when user adds product to cart
     */
    public void setOnAddToCart(ActionListener listener) {
        this.onAddToCart = listener;
    }

    /**
     * Set callback when user clicks info button
     */
    public void setOnViewInfo(ActionListener listener) {
        this.onViewInfo = listener;
    }

    /**
     * Get the product
     */
    public Product getProduct() {
        return product;
    }
}
