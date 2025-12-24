package com.hust.soict.aims.boundaries.customer.homepage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.controls.CartController;
import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductCardPanel extends JPanel {
    private final Product product;
    private final CartController cart;
    private final Component parentComponent;
    
    // Callbacks
    private ActionListener onAddToCart;
    private ActionListener onViewInfo;
    
    /**
     * Constructor
     * @param product Product to display
     * @param cart CartController for add to cart action
     * @param parent Parent component for dialog positioning
     */
    public ProductCardPanel(Product product, CartController cart, Component parent) {
        this.product = product;
        this.cart = cart;
        this.parentComponent = parent;
        
        setupUI();
    }
    
    /**
     * Setup the UI components and layout
     */
    private void setupUI() {
        setLayout(new BorderLayout(SPACING_SMALL, SPACING_SMALL));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT),
            PADDING_MEDIUM
        ));
        setBackground(BACKGROUND_WHITE);
        
        // Product title (top)
        JLabel titleLabel = new JLabel(product.getTitle());
        titleLabel.setFont(FONT_PRODUCT_NAME);
        titleLabel.setForeground(TEXT_PRIMARY);
        add(titleLabel, BorderLayout.NORTH);
        
        // Product info (center)
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, SPACING_XSMALL));
        infoPanel.setOpaque(false);
        
        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", product.getCurrentPrice()));
        priceLabel.setFont(FONT_BODY);
        priceLabel.setForeground(INFO_COLOR);
        
        JLabel weightLabel = new JLabel(String.format("Weight: %.2f kg", product.getWeight()));
        weightLabel.setFont(FONT_SMALL);
        weightLabel.setForeground(TEXT_SECONDARY);
        
        infoPanel.add(priceLabel);
        infoPanel.add(weightLabel);
        add(infoPanel, BorderLayout.CENTER);
        
        // Buttons (bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_XSMALL, 0));
        buttonPanel.setOpaque(false);
        
        JButton addButton = createAddButton();
        JButton infoButton = createInfoButton();
        
        buttonPanel.add(addButton);
        buttonPanel.add(infoButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create "Add to Cart" button
     */
    private JButton createAddButton() {
        JButton addButton = new JButton("Add");
        addButton.setFont(FONT_BUTTON);
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(TEXT_ON_PRIMARY);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(70, 35));
        addButton.setCursor(CURSOR_HAND);
        
        addButton.addActionListener(e -> {
            cart.addProduct(product, 1);
            
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
    private JButton createInfoButton() {
        JButton infoButton = new JButton("Info");
        infoButton.setFont(FONT_BUTTON);
        infoButton.setBackground(BACKGROUND_GRAY);
        infoButton.setForeground(TEXT_PRIMARY);
        infoButton.setFocusPainted(false);
        infoButton.setPreferredSize(BUTTON_SIZE_ICON);
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
