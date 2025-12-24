package com.hust.soict.aims.boundaries.customer.shipping;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.entities.CartItem;
import com.hust.soict.aims.entities.DeliveryInfo;

import static com.hust.soict.aims.utils.UIConstant.*;

public class OrderSummaryPanel extends JPanel {
    private final CartController cartController;
    private final PlaceOrderController placeOrderController;
    private JPanel itemsPanel;
    private JLabel totalItemsLabel;
    private JLabel subtotalLabel;
    private JLabel shippingLabel;
    private JLabel totalLabel;

    private double shippingFee = 0.0;

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
        updateSummary();
    }

    public OrderSummaryPanel(CartController cartController, PlaceOrderController placeOrderController) {
        this.cartController = cartController;
        this.placeOrderController = placeOrderController;
        setupUI();
    }

    /**
     * Setup the UI components
     */
    private void setupUI() {
        setLayout(new BorderLayout(0, SPACING_SMALL));
        setBackground(BACKGROUND_LIGHT);

        // Border with title
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_MEDIUM),
                "Order Summary");
        border.setTitleFont(FONT_HEADER);
        setBorder(BorderFactory.createCompoundBorder(border, PADDING_MEDIUM));

        // Top: Total items count
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_SMALL, 0));

        totalItemsLabel = new JLabel();
        totalItemsLabel.setFont(FONT_BODY);
        topPanel.add(totalItemsLabel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // Center: Items list
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(BACKGROUND_WHITE);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT));
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Bottom: Calculation summary
        JPanel bottomPanel = createCalculationPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Create calculation panel with subtotal, shipping, total
     */
    private JPanel createCalculationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_MEDIUM, 0, 0, 0));

        // Separator line
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        panel.add(separator);
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));

        // Subtotal row
        subtotalLabel = new JLabel();
        subtotalLabel.setFont(FONT_BODY);
        panel.add(createSummaryRow("Subtotal:", subtotalLabel));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_XSMALL)));

        // Shipping row
        shippingLabel = new JLabel("0 VND");
        shippingLabel.setFont(FONT_BODY);
        shippingLabel.setForeground(SUCCESS_COLOR);
        panel.add(createSummaryRow("Shipping:", shippingLabel));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));

        // Separator before total
        JSeparator separator2 = new JSeparator();
        separator2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        panel.add(separator2);
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));

        // Total row (larger font)
        totalLabel = new JLabel();
        totalLabel.setFont(FONT_TITLE);
        totalLabel.setForeground(INFO_COLOR);
        JPanel totalRow = createSummaryRow("Total:", totalLabel);
        totalRow.setBackground(new Color(240, 248, 255)); // Light blue highlight
        totalRow.setOpaque(true);
        totalRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INFO_COLOR, 1),
                BorderFactory.createEmptyBorder(SPACING_XSMALL, SPACING_SMALL, SPACING_XSMALL, SPACING_SMALL)));
        panel.add(totalRow);

        return panel;
    }

    /**
     * Create a summary row (label + value)
     */
    private JPanel createSummaryRow(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BODY);

        row.add(label, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);

        return row;
    }

    /**
     * Update the order summary display
     */
    public void updateSummary() {
        itemsPanel.removeAll();

        List<CartItem> items = cartController.getItems();
        int totalQty = cartController.getTotalQuantity();
        double subtotal = cartController.getSubtotal();
        double total = subtotal + shippingFee;

        // Update total items
        totalItemsLabel.setText(String.format("Items in cart: %d", totalQty));

        // Add each cart item as a card
        for (CartItem item : items) {
            JPanel itemCard = createItemCard(item);
            itemsPanel.add(itemCard);
            itemsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));
        }

        // Update calculation labels
        subtotalLabel.setText(String.format("%.0f VND", subtotal));
        shippingLabel.setText(String.format("%.0f VND", shippingFee));
        totalLabel.setText(String.format("%.0f VND", total));

        // Refresh UI
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    public void updateShippingFee(DeliveryInfo deliveryInfo) {
        if (deliveryInfo == null)
            return;

        PlaceOrderController.PlaceOrderResult result = placeOrderController.placeOrder(cartController, deliveryInfo);

        if (result.success && result.invoice != null) {
            this.shippingFee = result.invoice.getShippingFee();
            updateSummary();
        }
    }

    /**
     * Create a card for each cart item
     */
    private JPanel createItemCard(CartItem item) {
        JPanel card = new JPanel(new BorderLayout(SPACING_SMALL, SPACING_XSMALL));
        card.setBackground(BACKGROUND_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_SMALL, SPACING_SMALL, SPACING_SMALL)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Left: Product info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(item.getProduct().getTitle());
        nameLabel.setFont(FONT_PRODUCT_NAME);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel(String.format("Price: %.0f VND", item.getProduct().getCurrentPrice()));
        priceLabel.setFont(FONT_SMALL);
        priceLabel.setForeground(TEXT_SECONDARY);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel qtyLabel = new JLabel(String.format("Quantity: %d", item.getQuantity()));
        qtyLabel.setFont(FONT_SMALL);
        qtyLabel.setForeground(TEXT_SECONDARY);
        qtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        infoPanel.add(qtyLabel);

        // Right: Item subtotal
        double itemSubtotal = item.getProduct().getCurrentPrice() * item.getQuantity();
        JLabel subtotalLabel = new JLabel(String.format("%.0f VND", itemSubtotal));
        subtotalLabel.setFont(FONT_BUTTON_LARGE);
        subtotalLabel.setForeground(INFO_COLOR);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(subtotalLabel, BorderLayout.NORTH);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    /**
     * Get current total amount
     */
    public double getTotalAmount() {
        return cartController.getSubtotal();
    }
}
