package com.hust.soict.aims.boundaries.customer.cart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.hust.soict.aims.entities.CartItem;
import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.components.IconButton;
import com.hust.soict.aims.components.ProductImagePanel;
import static com.hust.soict.aims.utils.UIConstant.*;

public class CartItemPanel extends JPanel {

    private static final Dimension QTY_SIZE = new Dimension(32, 24);
    private static final Color QTY_BG = new Color(245, 245, 245);
    private static final Color QTY_BORDER = new Color(220, 220, 220);
    private Product product;
    private int quantity;
    private JLabel qtyLabel;
    private JLabel subtotalLabel;
    private ActionListener onQuantityChanged;
    private ActionListener onRemove;

    public CartItemPanel(CartItem cartItem) {
        this.product = cartItem.getProduct();
        this.quantity = cartItem.getQuantity();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(12, 0));
        setBackground(BACKGROUND_WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        add(createImagePanel(), BorderLayout.WEST);
        add(createInfoPanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.EAST);
    }

    private JComponent createImagePanel() {
        return new ProductImagePanel(
                product.getImagePath(),
                product.getId(),
                70);
    }

    private JPanel createInfoPanel() {
        JPanel panel = createVerticalPanel();

        JLabel name = new JLabel(product.getTitle());
        name.setFont(FONT_PRODUCT_NAME);

        JLabel price = new JLabel(
                String.format("Price: $%.2f", product.getCurrentPrice()));
        price.setFont(FONT_SMALL);
        price.setForeground(TEXT_SECONDARY);

        panel.add(Box.createVerticalGlue());
        panel.add(name);
        panel.add(price);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        panel.add(createPriceQtyPanel());
        panel.add(Box.createHorizontalStrut(14));
        panel.add(createRemoveButton());

        return panel;
    }

    private JPanel createPriceQtyPanel() {
        JPanel panel = createVerticalPanel();

        subtotalLabel = new JLabel(formatSubtotal());
        subtotalLabel.setFont(FONT_BUTTON_LARGE);
        subtotalLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel qtyPanel = createQuantityPanel();
        qtyPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(subtotalLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(qtyPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createQuantityPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panel.setOpaque(false);

        JButton left = createArrowButton("/icons/left_arrow.png", -1);
        JButton right = createArrowButton("/icons/right_arrow.png", 1);

        qtyLabel = createQtyLabel();

        panel.add(left);
        panel.add(qtyLabel);
        panel.add(right);

        return panel;
    }

    private JLabel createQtyLabel() {
        JLabel label = new JLabel(String.valueOf(quantity), SwingConstants.CENTER);
        label.setFont(FONT_BODY);
        label.setOpaque(true);
        label.setBackground(QTY_BG);
        label.setBorder(BorderFactory.createLineBorder(QTY_BORDER));
        label.setPreferredSize(QTY_SIZE);
        label.setMinimumSize(QTY_SIZE);
        return label;
    }

    private JButton createArrowButton(String icon, int delta) {
        JButton btn = new IconButton(icon, 16, PRIMARY_COLOR.brighter());
        btn.addActionListener(e -> updateQuantity(quantity + delta));
        return btn;
    }

    private JButton createRemoveButton() {
        JButton btn = new IconButton("/icons/trash.png", 18, PRIMARY_COLOR.brighter());
        btn.addActionListener(e -> fireRemoveEvent(e));
        return btn;
    }

    private void updateQuantity(int newQty) {
        if (newQty < 1 || newQty == quantity)
            return;

        quantity = newQty;
        qtyLabel.setText(String.valueOf(quantity));
        subtotalLabel.setText(formatSubtotal());

        fireQuantityChanged();
    }

    private String formatSubtotal() {
        return String.format("$%.2f",
                product.getCurrentPrice() * quantity);
    }

    private void fireQuantityChanged() {
        if (onQuantityChanged != null) {
            onQuantityChanged.actionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "quantityChanged"));
        }
    }

    private void fireRemoveEvent(ActionEvent e) {
        if (onRemove != null) {
            onRemove.actionPerformed(e);
        }
    }

    public int getCurrentQuantity() {
        return quantity;
    }

    public void setOnQuantityChanged(ActionListener listener) {
        this.onQuantityChanged = listener;
    }

    public void setOnRemove(ActionListener listener) {
        this.onRemove = listener;
    }

    private JPanel createVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }
}
