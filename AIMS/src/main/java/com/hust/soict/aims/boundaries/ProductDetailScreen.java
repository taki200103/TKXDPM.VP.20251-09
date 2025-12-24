package com.hust.soict.aims.boundaries;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.utils.ImageUtils;
import com.hust.soict.aims.utils.BarcodeGenerator;
import com.hust.soict.aims.controls.strategies.ProductDetailLoader;
import com.hust.soict.aims.controls.strategies.ProductDetailLoaderFactory;
import java.awt.image.BufferedImage;
import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductDetailScreen extends JDialog {
    
    /**
     * Strategy Pattern & Factory Pattern Usage:
     * 
     * This constructor uses:
     * 1. Factory Pattern (ProductDetailLoaderFactory) to get the appropriate loader
     * 2. Strategy Pattern (ProductDetailLoader) to load and display product-specific details
     * 
     * The Strategy Pattern allows us to:
     * - Encapsulate the algorithm for loading product details
     * - Switch between different loading strategies at runtime
     * - Add new product types without modifying this screen
     * 
     * The Factory Pattern allows us to:
     * - Centralize the creation of loaders
     * - Hide the complexity of selecting the right loader
     */
    public ProductDetailScreen(Frame owner, Product p) {
        super(owner, "Product Details", true);
        setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        setSize(700, 600);
        setLocationRelativeTo(owner);
        
        // Strategy Pattern: Load full product details from database
        Product productWithDetails = loadFullProductDetails(p);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        mainPanel.setBorder(PADDING_MEDIUM);
        mainPanel.setBackground(BACKGROUND_WHITE);
        
        // Top panel: Image and basic info
        JPanel topPanel = new JPanel(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        topPanel.setBackground(BACKGROUND_WHITE);
        
        // Left: Product image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(BACKGROUND_LIGHT);
        imagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            PADDING_MEDIUM
        ));
        imagePanel.setPreferredSize(new Dimension(250, 250));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Get image path (use ImageUtils to get path from product ID)
        String imagePath = productWithDetails.getImagePath();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = ImageUtils.getProductImagePath(productWithDetails.getId());
        } else {
            // Verify the path exists, if not try to get from ID
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                imagePath = ImageUtils.getProductImagePath(productWithDetails.getId());
            }
        }
        
        // Try to load image if path exists
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage();
                Image scaledImg = img.getScaledInstance(230, 230, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                imageLabel.setText("<html><center>No<br>Image</center></html>");
                imageLabel.setFont(FONT_BODY);
                imageLabel.setForeground(TEXT_SECONDARY);
            }
        } else {
            // Placeholder for no image
            imageLabel.setText("<html><center>No<br>Image</center></html>");
            imageLabel.setFont(FONT_BODY);
            imageLabel.setForeground(TEXT_SECONDARY);
        }
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Right: Basic product info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BACKGROUND_WHITE);
        infoPanel.setBorder(PADDING_SMALL);
        
        // Title
        JLabel titleLabel = new JLabel(p.getTitle());
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        
        // Barcode
        JPanel barcodePanel = new JPanel();
        barcodePanel.setLayout(new BoxLayout(barcodePanel, BoxLayout.Y_AXIS));
        barcodePanel.setBackground(BACKGROUND_WHITE);
        barcodePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel barcodeLabel = new JLabel("Barcode:");
        barcodeLabel.setFont(FONT_BODY);
        barcodeLabel.setForeground(TEXT_SECONDARY);
        barcodeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        barcodePanel.add(barcodeLabel);
        barcodePanel.add(Box.createVerticalStrut(SPACING_XSMALL));
        
        // Generate and display barcode image
        String barcodeText = productWithDetails.getBarcode() != null ? 
                           productWithDetails.getBarcode() : 
                           String.valueOf(productWithDetails.getId());
        BufferedImage barcodeImage = BarcodeGenerator.generateBarcodeImageWithText(barcodeText, 280, 60, true);
        
        JLabel barcodeImageLabel = new JLabel();
        if (barcodeImage != null) {
            ImageIcon barcodeIcon = new ImageIcon(barcodeImage);
            barcodeImageLabel.setIcon(barcodeIcon);
        } else {
            // Fallback to text if barcode generation fails
            barcodeImageLabel.setText(barcodeText);
            barcodeImageLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_BODY));
            barcodeImageLabel.setForeground(TEXT_PRIMARY);
            barcodeImageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                new EmptyBorder(5, 10, 5, 10)
            ));
        }
        barcodeImageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        barcodePanel.add(barcodeImageLabel);
        
        infoPanel.add(barcodePanel);
        infoPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));
        
        // Price
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(java.util.Locale.getDefault());
        symbols.setGroupingSeparator(',');
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###", symbols);
        JLabel priceLabel = new JLabel(String.format("Price: %s VND", df.format((long)productWithDetails.getCurrentPrice())));
        priceLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_PRODUCT_NAME));
        priceLabel.setForeground(PRIMARY_COLOR);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(priceLabel);
        
        if (productWithDetails.getOriginalValue() > productWithDetails.getCurrentPrice()) {
            JLabel originalPriceLabel = new JLabel(String.format("Original: %s VND", df.format((long)productWithDetails.getOriginalValue())));
            originalPriceLabel.setFont(FONT_SMALL);
            originalPriceLabel.setForeground(TEXT_SECONDARY);
            originalPriceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(originalPriceLabel);
        }
        
        infoPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));
        
        // Additional common info
        addInfoRow(infoPanel, "Quantity:", String.valueOf(productWithDetails.getQuantity()));
        addInfoRow(infoPanel, "Condition:", productWithDetails.getCondition());
        addInfoRow(infoPanel, "Status:", productWithDetails.getStatus());
        
        infoPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));
        
        // Type
        JLabel typeLabel = new JLabel("Type: " + getTypeName(productWithDetails));
        typeLabel.setFont(FONT_BODY);
        typeLabel.setForeground(TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(typeLabel);
        
        topPanel.add(imagePanel, BorderLayout.WEST);
        topPanel.add(infoPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center: Detailed information
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(BACKGROUND_WHITE);
        detailPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            "Product Details",
            0, 0,
            FONT_BODY,
            TEXT_PRIMARY
        ));
        
        // Common product info
        addDetailRow(detailPanel, "Weight:", String.format("%.2f kg", productWithDetails.getWeight()));
        addDetailRow(detailPanel, "Dimension:", productWithDetails.getDimension());
        if (productWithDetails.getHeight() != null && productWithDetails.getWidth() != null) {
            String dimDetail = String.format("H: %.1f cm × W: %.1f cm", 
                                           productWithDetails.getHeight(), 
                                           productWithDetails.getWidth());
            if (productWithDetails.getLength() != null) {
                dimDetail += String.format(" × L: %.1f cm", productWithDetails.getLength());
            }
            addDetailRow(detailPanel, "Dimensions (H×W×L):", dimDetail);
        }
        addDetailRow(detailPanel, "Description:", productWithDetails.getDescription());
        detailPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        
        // Strategy Pattern: Use the appropriate loader to display type-specific details
        // Factory Pattern: Get the loader using the factory
        ProductDetailLoader loader = ProductDetailLoaderFactory.getLoader(productWithDetails);
        if (loader != null) {
            // Strategy Pattern: Delegate to the specific strategy to display details
            loader.displayDetails(detailPanel, productWithDetails);
        }
        
        JScrollPane detailScrollPane = new JScrollPane(detailPanel);
        detailScrollPane.setBorder(null);
        detailScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(detailScrollPane, BorderLayout.CENTER);
        
        // Bottom: Close button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BACKGROUND_WHITE);
        com.hust.soict.aims.utils.RoundedButton close = new com.hust.soict.aims.utils.RoundedButton("Close", 8);
        close.setFont(FONT_BUTTON);
        close.setBackground(PRIMARY_COLOR);
        close.setForeground(TEXT_ON_PRIMARY);
        close.setCursor(CURSOR_HAND);
        close.setPreferredSize(BUTTON_SIZE_MEDIUM);
        close.addActionListener(e -> setVisible(false));
        bottomPanel.add(close);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Strategy Pattern: Load full product details from database
     * 
     * This method uses:
     * 1. Factory Pattern to get the appropriate loader
     * 2. Strategy Pattern to load product-specific details by joining with type-specific tables
     */
    private Product loadFullProductDetails(Product product) {
        String dbUrl = "jdbc:sqlite:aims.db";
        
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            // Factory Pattern: Get the appropriate loader for this product type
            ProductDetailLoader loader = ProductDetailLoaderFactory.getLoader(product);
            
            if (loader != null) {
                // Strategy Pattern: Use the specific strategy to load details
                // This will join with the appropriate table (Book, CD, DVD, Newspaper, Track)
                return loader.loadDetails(conn, product);
            }
        } catch (SQLException e) {
            System.err.println("Error loading product details: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Return original product if loading fails
        return product;
    }
    
    /**
     * Helper method to add info row in the info panel (top section)
     */
    private void addInfoRow(JPanel panel, String label, String value) {
        if (value == null || value.isEmpty()) return;
        
        JLabel infoLabel = new JLabel(label + " " + value);
        infoLabel.setFont(FONT_SMALL);
        infoLabel.setForeground(TEXT_SECONDARY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(infoLabel);
    }
    
    private String getTypeName(Product p) {
        if (p instanceof Book) return "Book";
        if (p instanceof CD) return "CD";
        if (p instanceof DVD) return "DVD";
        if (p instanceof Newspaper) return "Newspaper";
        return "Product";
    }
    
    /**
     * Static method to add detail row - used by Strategy Pattern implementations
     * This allows the strategy classes to add rows to the detail panel
     */
    public static void addDetailRow(JPanel panel, String label, String value) {
        if (value == null || value.isEmpty()) return;
        
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(BACKGROUND_WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(FONT_BODY);
        labelComp.setForeground(TEXT_SECONDARY);
        labelComp.setPreferredSize(new Dimension(150, 20));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(FONT_BODY);
        valueComp.setForeground(TEXT_PRIMARY);
        
        row.add(labelComp);
        row.add(valueComp);
        panel.add(row);
        panel.add(Box.createVerticalStrut(SPACING_XSMALL));
    }
}
