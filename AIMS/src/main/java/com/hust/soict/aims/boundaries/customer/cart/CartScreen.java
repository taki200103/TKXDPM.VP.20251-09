package com.hust.soict.aims.boundaries.customer.cart;

import javax.swing.*;
import java.awt.*;
import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.boundaries.customer.shipping.DeliveryInfoScreen;
import com.hust.soict.aims.components.RoundedButton;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.entities.CartItem;
import static com.hust.soict.aims.utils.UIConstant.*;

public class CartScreen extends BaseScreenHandler {

    private final CartController cartController;

    private JPanel itemsPanel;
    private JLabel totalItemsLabel;
    private JLabel subtotalLabel;
    private RoundedButton placeOrderButton;

    public CartScreen(CartController cartController, BaseScreenHandler parent) {
        super("Shopping Cart", parent, false);
        this.cartController = cartController;
        initializeScreen();
    }

    @Override
    protected void initComponents() {
        itemsPanel = createItemsPanel();

        totalItemsLabel = createLabel("Total Items: 0", FONT_BODY);
        subtotalLabel = createLabel("Subtotal: 0 VNĐ", FONT_HEADER, INFO_COLOR);

        placeOrderButton = createPlaceOrderButton();
    }

    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createItemsScrollPane(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    @Override
    protected void bindEvents() {
        placeOrderButton.addActionListener(e -> handlePlaceOrder());
    }

    private JPanel createItemsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_WHITE);
        return panel;
    }

    private JScrollPane createItemsScrollPane() {
        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(PADDING_SMALL);
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(0, HEADER_HEIGHT));

        JLabel title = new JLabel("Shopping Cart");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_ON_PRIMARY);

        header.add(title, BorderLayout.CENTER);
        return createHeaderWithNavigation(header);
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout(SPACING_SMALL, SPACING_SMALL));
        footer.setBorder(PADDING_SMALL);
        footer.setBackground(BACKGROUND_LIGHT);

        JPanel summary = new JPanel(new GridLayout(2, 1, SPACING_XSMALL, SPACING_XSMALL));
        summary.setOpaque(false);
        summary.add(totalItemsLabel);
        summary.add(subtotalLabel);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(placeOrderButton);

        footer.add(summary, BorderLayout.WEST);
        footer.add(actions, BorderLayout.EAST);

        return footer;
    }

    private RoundedButton createPlaceOrderButton() {
        RoundedButton btn = new RoundedButton("Place Order");
        btn.setFont(FONT_BUTTON_LARGE);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(TEXT_ON_PRIMARY);
        btn.setPreferredSize(BUTTON_SIZE_LARGE);
        btn.setCursor(CURSOR_HAND);
        btn.setFocusPainted(false);
        return btn;
    }

    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        refresh();
    }

    @Override
    public void refresh() {
        buildItemPanels();
        updateSummary();
        updateActionState();

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private void buildItemPanels() {
        itemsPanel.removeAll();

        for (CartItem item : cartController.getItems()) {
            CartItemPanel panel = new CartItemPanel(item);
            bindItemEvents(panel, item);
            itemsPanel.add(panel);
        }
    }

    private void bindItemEvents(CartItemPanel panel, CartItem item) {

        panel.setOnQuantityChanged(e -> {
            cartController.updateQuantity(
                    item.getProduct().getId(),
                    panel.getCurrentQuantity());
            updateSummary();
        });

        panel.setOnRemove(e -> handleRemoveItem(item));
    }

    private void handleRemoveItem(CartItem item) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Remove " + item.getProduct().getTitle() + " from cart?",
                "Remove Item",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            cartController.remove(item.getProduct().getId());
            refresh();
        }
    }

    private void handlePlaceOrder() {
        if (cartController.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cart is empty! Please add items before placing order.",
                    "Cart Empty",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (CartItem item : cartController.getItems()) {
            int orderedQty = item.getQuantity();
            int stockQty = item.getProduct().getQuantity();

            if (orderedQty > stockQty) {
                JOptionPane.showMessageDialog(
                        this,
                        "Product \"" + item.getProduct().getTitle() + "\" only has "
                                + stockQty + " item(s) left in stock.\n"
                                + "Please adjust the quantity.",
                        "Insufficient Stock",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        navigateTo(new DeliveryInfoScreen(
                this,
                cartController,
                new PlaceOrderController()));
    }

    private void updateSummary() {
        totalItemsLabel.setText(
                "Total Items: " + cartController.getTotalQuantity());
        subtotalLabel.setText(
                String.format("Subtotal: %.0f VNĐ", cartController.getSubtotal()));
    }

    private void updateActionState() {
        placeOrderButton.setEnabled(!cartController.isEmpty());
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = createLabel(text, font);
        label.setForeground(color);
        return label;
    }
}
