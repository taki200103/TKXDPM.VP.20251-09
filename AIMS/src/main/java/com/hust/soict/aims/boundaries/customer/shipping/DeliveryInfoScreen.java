package com.hust.soict.aims.boundaries.customer.shipping;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.boundaries.customer.invoice.InvoiceScreen;
import com.hust.soict.aims.entities.DeliveryInfo;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.controls.PlaceOrderController;

import static com.hust.soict.aims.utils.UIConstant.*;

public class DeliveryInfoScreen extends BaseScreenHandler {
    private final CartController cartController;
    private final PlaceOrderController placeOrderController;
    private DeliveryInfo deliveryInfo;
    
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField cityField;
    private JTextField districtField;
    private JTextArea addressArea;
    
    private OrderSummaryPanel orderSummaryPanel;
    
    private JButton confirmButton;
    
    public DeliveryInfoScreen(BaseScreenHandler parent, CartController cartController, 
                             PlaceOrderController placeOrderController) {
        super("Delivery Information", parent, false);
        this.cartController = cartController;
        this.placeOrderController = placeOrderController;
        
        initializeScreen();
    }
    
    @Override
    protected void initComponents() {
        // Form fields
        nameField = new JTextField();
        nameField.setFont(FONT_BODY);
        nameField.setPreferredSize(INPUT_SIZE_LARGE);
        
        phoneField = new JTextField();
        phoneField.setFont(FONT_BODY);
        phoneField.setPreferredSize(INPUT_SIZE_LARGE);
        
        emailField = new JTextField();
        emailField.setFont(FONT_BODY);
        emailField.setPreferredSize(INPUT_SIZE_LARGE);
        
        cityField = new JTextField();
        cityField.setFont(FONT_BODY);
        cityField.setPreferredSize(INPUT_SIZE_LARGE);
        
        districtField = new JTextField();
        districtField.setFont(FONT_BODY);
        districtField.setPreferredSize(INPUT_SIZE_LARGE);
        
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(FONT_BODY);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        
        // Order summary component
        orderSummaryPanel = new OrderSummaryPanel(cartController);
        
        // Buttons
        confirmButton = new JButton("Confirm Order");
        confirmButton.setFont(FONT_BUTTON_LARGE);
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(TEXT_ON_PRIMARY);
        confirmButton.setFocusPainted(false);
        confirmButton.setPreferredSize(BUTTON_SIZE_LARGE);
        confirmButton.setCursor(CURSOR_HAND);
    }
    
    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        
        // Main Header Panel (without navigation)
        JPanel mainHeaderPanel = new JPanel(new BorderLayout());
        mainHeaderPanel.setBackground(PRIMARY_COLOR);
        mainHeaderPanel.setBorder(PADDING_MEDIUM);
        mainHeaderPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        
        // Center: Title
        JLabel titleLabel = new JLabel("Delivery Information");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainHeaderPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Combine top navigation bar + main header
        JPanel headerWithNav = createHeaderWithNavigation(mainHeaderPanel);
        add(headerWithNav, BorderLayout.NORTH);
        
        // Center: Split into form (left) and cart summary (right)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, SPACING_MEDIUM, 0));
        centerPanel.setBorder(PADDING_MEDIUM);
        
        // Left: Delivery Form
        JPanel formPanel = createFormPanel();
        centerPanel.add(formPanel);
        
        // Right: Order Summary
        centerPanel.add(orderSummaryPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Footer: Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_MEDIUM, SPACING_MEDIUM));
        footerPanel.setBackground(BACKGROUND_LIGHT);
        footerPanel.add(confirmButton);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create delivery information form panel
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_WHITE);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM),
            "Recipient Information"
        );
        border.setTitleFont(FONT_HEADER);
        panel.setBorder(BorderFactory.createCompoundBorder(border, PADDING_MEDIUM));
        
        // Add form fields
        panel.add(createFieldRow("Receiver Name: *", nameField));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));
        
        panel.add(createFieldRow("Phone Number: *", phoneField));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));
        
        panel.add(createFieldRow("Email: *", emailField));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));
        
        panel.add(createFieldRow("City: *", cityField));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));
        
        panel.add(createFieldRow("District/Ward: *", districtField));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));
        
        panel.add(createFieldRow("Detailed Address: *", new JScrollPane(addressArea)));
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    /**
     * Create a row for form field (label + component)
     */
    private JPanel createFieldRow(String labelText, JComponent component) {
        JPanel row = new JPanel(new BorderLayout(SPACING_SMALL, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BODY);
        label.setPreferredSize(new Dimension(140, 25));
        
        row.add(label, BorderLayout.WEST);
        row.add(component, BorderLayout.CENTER);
        
        return row;
    }
    
    @Override
    protected void bindEvents() {
        // Confirm button
        confirmButton.addActionListener(e -> handleConfirm());
    }
    
    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        orderSummaryPanel.updateSummary();
    }
    
    /**
     * Handle confirm button click
     */
    private void handleConfirm() {
        // Validate required fields
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String city = cityField.getText().trim();
        String address = addressArea.getText().trim();
        
        if (name.isEmpty() || phone.isEmpty() || city.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill all required fields (*)",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate phone number
        if (!phone.matches("[0-9+\\- ]{7,15}")) {
            JOptionPane.showMessageDialog(this,
                "Invalid phone number format. Please enter 7-15 digits.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
                return;
            }
        
        // Validate email if provided
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(this,
                "Invalid email format.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
                return;
            }

        // Create delivery info
        deliveryInfo = new DeliveryInfo();
        deliveryInfo.setReceiverName(name);
        deliveryInfo.setPhone(phone);
        deliveryInfo.setCity(city);
        deliveryInfo.setDistrict(districtField.getText().trim());
        deliveryInfo.setAddressLine(address);
        
        // Create order and invoice (stock will be reduced after payment)
        PlaceOrderController.PlaceOrderResult result = placeOrderController.placeOrder(cartController, deliveryInfo);
        
        if (!result.success) {
            JOptionPane.showMessageDialog(this, 
                result.message, 
                "Order Failed", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Navigate to InvoiceScreen (pass PlaceOrderController and CartController)
        InvoiceScreen invoiceScreen = new InvoiceScreen(
            this, result.invoice, placeOrderController, cartController
        );
        navigateTo(invoiceScreen);
    }
    
    /**
     * Get the delivery info (null if cancelled)
     */
    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }
}
