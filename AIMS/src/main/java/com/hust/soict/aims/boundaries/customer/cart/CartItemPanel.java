package com.hust.soict.aims.boundaries.customer.cart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import com.hust.soict.aims.entities.CartItem;
import static com.hust.soict.aims.utils.UIConstant.*;

public class CartItemPanel extends JPanel {
    private final CartItem cartItem;
    
    private JSpinner qtySpinner;
    private JLabel subtotalLabel;
    private JButton removeButton;
    
    // Callbacks
    private ActionListener onQuantityChanged;
    private ActionListener onRemove;
    
    /**
     * Constructor
     */
    public CartItemPanel(CartItem cartItem) {
        this.cartItem = cartItem;
        setupUI();
        bindEvents();
    }
    
    /**
     * Setup the UI components
     */
    private void setupUI() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT),
            PADDING_MEDIUM
        ));
        setBackground(BACKGROUND_WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, CART_ITEM_HEIGHT));
        
        // Left: Product info
        JPanel leftPanel = createProductInfoPanel();
        
        // Center: Quantity control
        JPanel centerPanel = createQuantityPanel();
        
        // Right: Subtotal and Remove button
        JPanel rightPanel = createActionsPanel();
        
        // Add all sections with proper spacing
        add(leftPanel);
        add(Box.createRigidArea(new Dimension(SPACING_MEDIUM, 0)));
        add(centerPanel);
        add(Box.createHorizontalGlue());
        add(rightPanel);
    }
    
    /**
     * Create product info panel (left)
     */
    private JPanel createProductInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, SPACING_XSMALL));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(CART_PRODUCT_WIDTH, 50));
        panel.setMinimumSize(new Dimension(CART_PRODUCT_WIDTH, 50));
        panel.setMaximumSize(new Dimension(CART_PRODUCT_WIDTH, 50));
        
        JLabel nameLabel = new JLabel(cartItem.getProduct().getTitle());
        nameLabel.setFont(FONT_PRODUCT_NAME);
        
        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", 
            cartItem.getProduct().getCurrentPrice()));
        priceLabel.setFont(FONT_SMALL);
        priceLabel.setForeground(TEXT_SECONDARY);
        
        panel.add(nameLabel);
        panel.add(priceLabel);
        
        return panel;
    }
    
    /**
     * Create quantity control panel (center)
     */
    private JPanel createQuantityPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_XSMALL, SPACING_SMALL));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(CART_QUANTITY_WIDTH, 50));
        panel.setMinimumSize(new Dimension(CART_QUANTITY_WIDTH, 50));
        panel.setMaximumSize(new Dimension(CART_QUANTITY_WIDTH, 50));
        
        JLabel qtyLabel = new JLabel("Qty:");
        qtyLabel.setFont(FONT_SMALL);
        qtyLabel.setPreferredSize(new Dimension(35, 25));
        qtyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        qtySpinner = new JSpinner(new SpinnerNumberModel(
            cartItem.getQuantity(), 1, 100, 1
        ));
        qtySpinner.setPreferredSize(INPUT_SIZE_SMALL);
        qtySpinner.setFont(FONT_BODY);
        
        panel.add(qtyLabel);
        panel.add(qtySpinner);
        
        return panel;
    }
    
    /**
     * Create actions panel (right) - subtotal and remove button
     */
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, SPACING_XSMALL));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(CART_ACTIONS_WIDTH, 50));
        panel.setMinimumSize(new Dimension(CART_ACTIONS_WIDTH, 50));
        panel.setMaximumSize(new Dimension(CART_ACTIONS_WIDTH, 50));
        
        // Subtotal label
        double itemSubtotal = cartItem.getProduct().getCurrentPrice() * cartItem.getQuantity();
        subtotalLabel = new JLabel(String.format("$%.2f", itemSubtotal));
        subtotalLabel.setFont(FONT_BUTTON_LARGE);
        subtotalLabel.setForeground(INFO_COLOR);
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Remove button
        removeButton = new JButton("Remove");
        removeButton.setPreferredSize(BUTTON_SIZE_MEDIUM);
        removeButton.setBackground(DANGER_COLOR);
        removeButton.setForeground(TEXT_ON_PRIMARY);
        removeButton.setFont(FONT_BUTTON);
        removeButton.setFocusPainted(false);
        removeButton.setCursor(CURSOR_HAND);
        
        panel.add(subtotalLabel);
        panel.add(removeButton);
        
        return panel;
    }
    
    /**
     * Bind events
     */
    private void bindEvents() {
        // Quantity spinner change
        qtySpinner.addChangeListener(e -> {
            int newQty = (Integer) qtySpinner.getValue();
            updateSubtotal(newQty);
            
            // Trigger callback if set
            if (onQuantityChanged != null) {
                onQuantityChanged.actionPerformed(null);
            }
        });
        
        // Remove button click
        removeButton.addActionListener(e -> {
            // Trigger callback if set
            if (onRemove != null) {
                onRemove.actionPerformed(e);
            }
        });
    }
    
    /**
     * Update subtotal label
     */
    private void updateSubtotal(int quantity) {
        double itemSubtotal = cartItem.getProduct().getCurrentPrice() * quantity;
        subtotalLabel.setText(String.format("$%.2f", itemSubtotal));
    }
    
    /**
     * Set callback when quantity changes
     */
    public void setOnQuantityChanged(ActionListener listener) {
        this.onQuantityChanged = listener;
    }
    
    /**
     * Set callback when remove button is clicked
     */
    public void setOnRemove(ActionListener listener) {
        this.onRemove = listener;
    }
    
    /**
     * Get cart item
     */
    public CartItem getCartItem() {
        return cartItem;
    }
    
    /**
     * Get current quantity from spinner
     */
    public int getCurrentQuantity() {
        return (Integer) qtySpinner.getValue();
    }
    
    /**
     * Update quantity programmatically
     */
    public void setQuantity(int quantity) {
        qtySpinner.setValue(quantity);
        updateSubtotal(quantity);
    }
}
