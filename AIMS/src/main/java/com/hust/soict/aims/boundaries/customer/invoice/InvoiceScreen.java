package com.hust.soict.aims.boundaries.customer.invoice;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.boundaries.customer.payment.PaymentScreen;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.CartItem;

import static com.hust.soict.aims.utils.UIConstant.*;

public class InvoiceScreen extends BaseScreenHandler {
    private boolean paid = false;
    private final Invoice invoice;
    private final PlaceOrderController placeOrderController;
    private final CartController cartController;
    
    private JPanel invoiceDetailsPanel;
    private JButton payButton;

    public InvoiceScreen(BaseScreenHandler parent, Invoice invoice, 
                        PlaceOrderController placeOrderController, CartController cartController) {
        super("Invoice & Payment", parent, false);
        
        this.invoice = invoice;
        this.placeOrderController = placeOrderController;
        this.cartController = cartController;
        
        initializeScreen();
    }
    
    @Override
    protected void initComponents() {
        // Invoice details panel
        invoiceDetailsPanel = new JPanel();
        invoiceDetailsPanel.setLayout(new BoxLayout(invoiceDetailsPanel, BoxLayout.Y_AXIS));
        invoiceDetailsPanel.setBackground(BACKGROUND_WHITE);
        
        // Add invoice items using InvoiceItemPanel component
        JPanel itemsSection = createItemsSection();
        invoiceDetailsPanel.add(itemsSection);
        invoiceDetailsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_LARGE)));
        
        // Add payment summary using PaymentSummaryPanel component
        PaymentSummaryPanel paymentSummaryPanel = new PaymentSummaryPanel(invoice);
        paymentSummaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        invoiceDetailsPanel.add(paymentSummaryPanel);

        // Pay button
        payButton = new JButton("Proceed to Payment");
        payButton.setFont(FONT_BUTTON_LARGE);
        payButton.setBackground(PRIMARY_COLOR);
        payButton.setForeground(TEXT_ON_PRIMARY);
        payButton.setFocusPainted(false);
        payButton.setPreferredSize(BUTTON_SIZE_LARGE);
        payButton.setCursor(CURSOR_HAND);
    }
    
    private JPanel createItemsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BACKGROUND_WHITE);
        
        // Use TitledBorder to match Payment Summary style
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 2),
            "Order Items"
        );
        border.setTitleFont(FONT_HEADER);
        border.setTitleColor(PRIMARY_COLOR);
        section.setBorder(BorderFactory.createCompoundBorder(border, PADDING_LARGE));
        
        // Items container
        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setBackground(BACKGROUND_WHITE);
        
        // Add each item using InvoiceItemPanel
        List<CartItem> items = invoice.getOrder().getItems();
        for (CartItem item : items) {
            InvoiceItemPanel itemPanel = new InvoiceItemPanel(item);
            itemsContainer.add(itemPanel);
        }
        
        section.add(itemsContainer, BorderLayout.CENTER);
        
        return section;
    }
    
    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        
        // Main Header Panel
        JPanel mainHeaderPanel = new JPanel(new BorderLayout());
        mainHeaderPanel.setBackground(PRIMARY_COLOR);
        mainHeaderPanel.setBorder(PADDING_SMALL);
        mainHeaderPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        
        JLabel titleLabel = new JLabel("Invoice & Payment");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainHeaderPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Combine top navigation bar + main header
        JPanel headerWithNav = createHeaderWithNavigation(mainHeaderPanel);
        add(headerWithNav, BorderLayout.NORTH);
        
        // Center - Invoice Details
        JScrollPane scrollPane = new JScrollPane(invoiceDetailsPanel);
        scrollPane.setBorder(PADDING_SMALL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer - Pay Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_MEDIUM, SPACING_MEDIUM));
        footerPanel.setBackground(BACKGROUND_LIGHT);
        footerPanel.add(payButton);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    @Override
    protected void bindEvents() {
        payButton.addActionListener(e -> {
            // Navigate to PaymentScreen
            PaymentScreen paymentScreen = new PaymentScreen(
                this, invoice, placeOrderController, cartController
            );
            navigateTo(paymentScreen);
        });
    }

    public boolean isPaid() { 
        return paid; 
    }
}
