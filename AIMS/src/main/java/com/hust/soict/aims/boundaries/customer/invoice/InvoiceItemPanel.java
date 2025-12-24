package com.hust.soict.aims.boundaries.customer.invoice;

import javax.swing.*;
import java.awt.*;

import com.hust.soict.aims.entities.CartItem;

import static com.hust.soict.aims.utils.UIConstant.*;

public class InvoiceItemPanel extends JPanel {
    private final CartItem cartItem;
    
    public InvoiceItemPanel(CartItem cartItem) {
        this.cartItem = cartItem;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(SPACING_MEDIUM, 0));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
            BorderFactory.createEmptyBorder(SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM)
        ));
        setBackground(BACKGROUND_WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Left: Product info
        JPanel leftPanel = createProductInfoPanel();
        
        // Center: Quantity and price
        JPanel centerPanel = createQuantityPricePanel();
        
        // Right: Subtotal
        JPanel rightPanel = createSubtotalPanel();
        
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private JPanel createProductInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(350, 60));
        
        JLabel nameLabel = new JLabel(cartItem.getProduct().getTitle());
        nameLabel.setFont(FONT_PRODUCT_NAME);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel typeLabel = new JLabel("Type: " + cartItem.getProduct().getType());
        typeLabel.setFont(FONT_SMALL);
        typeLabel.setForeground(TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(typeLabel);
        
        return panel;
    }
    
    private JPanel createQuantityPricePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, SPACING_SMALL, 5));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(200, 60));
        
        JLabel qtyTitleLabel = new JLabel("Quantity:");
        qtyTitleLabel.setFont(FONT_SMALL);
        qtyTitleLabel.setForeground(TEXT_SECONDARY);
        
        JLabel qtyValueLabel = new JLabel(String.valueOf(cartItem.getQuantity()));
        qtyValueLabel.setFont(FONT_BODY);
        qtyValueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        JLabel priceTitleLabel = new JLabel("Price:");
        priceTitleLabel.setFont(FONT_SMALL);
        priceTitleLabel.setForeground(TEXT_SECONDARY);
        
        JLabel priceValueLabel = new JLabel(String.format("$%.2f", cartItem.getProduct().getCurrentPrice()));
        priceValueLabel.setFont(FONT_BODY);
        priceValueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        panel.add(qtyTitleLabel);
        panel.add(qtyValueLabel);
        panel.add(priceTitleLabel);
        panel.add(priceValueLabel);
        
        return panel;
    }
    
    private JPanel createSubtotalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(120, 60));
        
        double itemSubtotal = cartItem.getProduct().getCurrentPrice() * cartItem.getQuantity();
        JLabel subtotalLabel = new JLabel(String.format("$%.2f", itemSubtotal));
        subtotalLabel.setFont(FONT_BUTTON_LARGE);
        subtotalLabel.setForeground(INFO_COLOR);
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panel.add(subtotalLabel, BorderLayout.NORTH);
        
        return panel;
    }
}
