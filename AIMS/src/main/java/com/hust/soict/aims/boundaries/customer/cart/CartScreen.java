package com.hust.soict.aims.boundaries.customer.cart;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.boundaries.customer.shipping.DeliveryInfoScreen;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.entities.CartItem;

import static com.hust.soict.aims.utils.UIConstant.*;

public class CartScreen extends BaseScreenHandler {
    private final CartController cartController;
    
    private JPanel itemsPanel;
    private JLabel totalItemsLabel;
    private JLabel subtotalLabel;
    private JButton placeOrderButton;

    public CartScreen(CartController cartController, BaseScreenHandler parent) {
        super("Shopping Cart", parent, false);
        
        this.cartController = cartController;
        
        initializeScreen();
    }

    @Override
    protected void initComponents() {
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(BACKGROUND_WHITE);
        
        totalItemsLabel = new JLabel("Total Items: 0");
        totalItemsLabel.setFont(FONT_BODY);
        
        subtotalLabel = new JLabel("Subtotal: $0.00");
        subtotalLabel.setFont(FONT_HEADER);
        subtotalLabel.setForeground(INFO_COLOR);

        placeOrderButton = new JButton("Place Order");
        placeOrderButton.setFont(FONT_BUTTON_LARGE);
        placeOrderButton.setBackground(PRIMARY_COLOR);  
        placeOrderButton.setForeground(TEXT_ON_PRIMARY);
        placeOrderButton.setFocusPainted(false);
        placeOrderButton.setPreferredSize(BUTTON_SIZE_LARGE);
        placeOrderButton.setCursor(CURSOR_HAND);
    }

    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(SPACING_SMALL, SPACING_SMALL));

        // Main Header Panel
        JPanel mainHeaderPanel = new JPanel(new BorderLayout());
        mainHeaderPanel.setBorder(PADDING_SMALL);
        mainHeaderPanel.setBackground(PRIMARY_COLOR);
        mainHeaderPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        
        // Center: Title
        JLabel titleLabel = new JLabel("Your Shopping Cart");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainHeaderPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Combine top navigation bar + main header
        JPanel headerWithNav = createHeaderWithNavigation(mainHeaderPanel);
        add(headerWithNav, BorderLayout.NORTH);
        
        // Center Panel - Cart Items
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(PADDING_SMALL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer Panel - Summary and Actions
        JPanel footerPanel = new JPanel(new BorderLayout(SPACING_SMALL, SPACING_SMALL));
        footerPanel.setBorder(PADDING_SMALL);
        footerPanel.setBackground(BACKGROUND_LIGHT);
        
        // Summary Panel (left side)
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1, SPACING_XSMALL, SPACING_XSMALL));
        summaryPanel.setOpaque(false);
        summaryPanel.add(totalItemsLabel);
        summaryPanel.add(subtotalLabel);
        
        // Action Panel (right side)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        actionPanel.add(placeOrderButton);
        
        footerPanel.add(summaryPanel, BorderLayout.WEST);
        footerPanel.add(actionPanel, BorderLayout.EAST);
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void bindEvents() {
        // Place order button
        placeOrderButton.addActionListener(e -> {
            if (cartController.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Cart is empty! Please add items before placing order.", 
                    "Cart Empty", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Navigate to DeliveryInfoScreen
            DeliveryInfoScreen deliveryInfoScreen = new DeliveryInfoScreen(
                this, 
                cartController, 
                new PlaceOrderController()
            );
            navigateTo(deliveryInfoScreen);
        });
    }
    
    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        refresh();
    }

    @Override
    public void refresh() {
        itemsPanel.removeAll();
        
        List<CartItem> items = cartController.getItems();
        
        // Add each cart item using CartItemPanel component
        for (CartItem item : items) {
            CartItemPanel itemPanel = new CartItemPanel(item);
            
            // Set callback for quantity change
            itemPanel.setOnQuantityChanged(e -> {
                int newQty = itemPanel.getCurrentQuantity();
                cartController.updateQuantity(item.getProduct().getId(), newQty);
                refresh();
            });
            
            // Set callback for remove button
            itemPanel.setOnRemove(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Remove " + item.getProduct().getTitle() + " from cart?",
                    "Remove Item",
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    cartController.remove(item.getProduct().getId());
                    refresh();
                }
            });
            
            itemsPanel.add(itemPanel);
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Update summary
        int totalItems = cartController.getTotalQuantity();
        double subtotal = cartController.getSubtotal();
        
        totalItemsLabel.setText(String.format("Total Items: %d", totalItems));
        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
        
        // Enable/disable buttons
        placeOrderButton.setEnabled(!cartController.isEmpty());
        
        // Refresh UI
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
}
