package com.hust.soict.aims.boundaries.customer.shipping;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.boundaries.customer.invoice.InvoiceScreen;
import com.hust.soict.aims.components.RoundedButton;
import com.hust.soict.aims.entities.DeliveryInfo;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.data.VietnamAddressData;

import static com.hust.soict.aims.utils.UIConstant.*;

public class DeliveryInfoScreen extends BaseScreenHandler {
    private final CartController cartController;
    private final PlaceOrderController placeOrderController;
    private DeliveryInfo deliveryInfo;

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> cityComboBox;
    private JComboBox<String> districtComboBox;
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
        nameField = new JTextField();
        nameField.setFont(FONT_BODY);
        nameField.setPreferredSize(INPUT_SIZE_LARGE);

        phoneField = new JTextField();
        phoneField.setFont(FONT_BODY);
        phoneField.setPreferredSize(INPUT_SIZE_LARGE);

        emailField = new JTextField();
        emailField.setFont(FONT_BODY);
        emailField.setPreferredSize(INPUT_SIZE_LARGE);

        cityComboBox = new JComboBox<>();
        cityComboBox.setFont(FONT_BODY);
        cityComboBox.setPreferredSize(INPUT_SIZE_LARGE);
        cityComboBox.setEditable(true);

        districtComboBox = new JComboBox<>();
        districtComboBox.setFont(FONT_BODY);
        districtComboBox.setPreferredSize(INPUT_SIZE_LARGE);
        districtComboBox.setEditable(true);

        for (String city : VietnamAddressData.getCities()) {
            cityComboBox.addItem(city);
        }

        addressArea = new JTextArea(3, 20);
        addressArea.setFont(FONT_BODY);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        orderSummaryPanel = new OrderSummaryPanel(cartController, placeOrderController);

        confirmButton = new RoundedButton("Confirm Order");
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

        JPanel mainHeaderPanel = new JPanel(new BorderLayout());
        mainHeaderPanel.setBackground(PRIMARY_COLOR);
        mainHeaderPanel.setBorder(PADDING_MEDIUM);
        mainHeaderPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));

        JLabel titleLabel = new JLabel("Delivery Information");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        mainHeaderPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel headerWithNav = createHeaderWithNavigation(mainHeaderPanel);
        add(headerWithNav, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, SPACING_MEDIUM, 0));
        centerPanel.setBorder(PADDING_MEDIUM);

        JPanel formPanel = createFormPanel();
        centerPanel.add(formPanel);

        centerPanel.add(orderSummaryPanel);

        add(centerPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_MEDIUM, SPACING_MEDIUM));
        footerPanel.setBackground(BACKGROUND_LIGHT);
        footerPanel.add(confirmButton);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_WHITE);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_MEDIUM),
                "Recipient Information");
        border.setTitleFont(FONT_HEADER);
        panel.setBorder(BorderFactory.createCompoundBorder(border, PADDING_MEDIUM));

        panel.add(createFieldRow("Receiver Name: *", nameField));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));

        panel.add(createFieldRow("Phone Number: *", phoneField));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));

        panel.add(createFieldRow("Email: *", emailField));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));

        panel.add(createFieldRow("City: *", cityComboBox));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));

        panel.add(createFieldRow("District/Ward: *", districtComboBox));
        panel.add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));

        panel.add(createFieldRow("Detailed Address: *", new JScrollPane(addressArea)));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

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
        confirmButton.addActionListener(e -> handleConfirm());
        cityComboBox.addActionListener(e -> {
            districtComboBox.removeAllItems();
            String selectedCity = (String) cityComboBox.getSelectedItem();

            if (selectedCity != null) {
                for (String d : VietnamAddressData.getDistricts(selectedCity)) {
                    districtComboBox.addItem(d);
                }

                DeliveryInfo previewInfo = buildPreviewDeliveryInfo();
                orderSummaryPanel.updateShippingFee(previewInfo);
            }
        });

    }

    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        orderSummaryPanel.updateSummary();
    }

    private void handleConfirm() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String city = (String) cityComboBox.getSelectedItem();
        String district = (String) districtComboBox.getSelectedItem();
        String address = addressArea.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || city.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all required fields (*)",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!phone.matches("[0-9+\\- ]{7,15}")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid phone number format. Please enter 7-15 digits.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid email format.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        deliveryInfo = new DeliveryInfo();
        deliveryInfo.setReceiverName(name);
        deliveryInfo.setPhone(phone);
        deliveryInfo.setEmail(email); // Set email from input field
        deliveryInfo.setCity(city);
        deliveryInfo.setDistrict(district != null ? district : "");
        deliveryInfo.setAddressLine(address);

        PlaceOrderController.PlaceOrderResult result = placeOrderController.placeOrder(cartController, deliveryInfo);

        if (!result.success) {
            JOptionPane.showMessageDialog(this,
                    result.message,
                    "Order Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        InvoiceScreen invoiceScreen = new InvoiceScreen(
                this, result.invoice, placeOrderController, cartController);
        navigateTo(invoiceScreen);
    }

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    private DeliveryInfo buildPreviewDeliveryInfo() {
        DeliveryInfo info = new DeliveryInfo();
        info.setCity((String) cityComboBox.getSelectedItem());
        info.setDistrict((String) districtComboBox.getSelectedItem());
        info.setAddressLine(addressArea.getText().trim());
        return info;
    }
}
