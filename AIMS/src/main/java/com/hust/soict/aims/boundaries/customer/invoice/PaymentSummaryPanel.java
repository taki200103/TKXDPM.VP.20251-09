package com.hust.soict.aims.boundaries.customer.invoice;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import com.hust.soict.aims.entities.Invoice;

import static com.hust.soict.aims.utils.UIConstant.*;

public class PaymentSummaryPanel extends JPanel {
    private final Invoice invoice;
    
    private JLabel subtotalLabel;
    private JLabel shippingLabel;
    private JLabel totalLabel;
    
    public PaymentSummaryPanel(Invoice invoice) {
        this.invoice = invoice;
        setupUI();
        updateSummary();
    }
    
    private void setupUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND_WHITE);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 2),
            "Payment Summary"
        );
        border.setTitleFont(FONT_HEADER);
        border.setTitleColor(PRIMARY_COLOR);
        setBorder(BorderFactory.createCompoundBorder(border, PADDING_LARGE));
        
        // Header with subtitle
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, SPACING_SMALL));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel headerLabel = new JLabel("Payment details");
        headerLabel.setFont(FONT_BODY);
        headerLabel.setForeground(TEXT_SECONDARY);
        headerPanel.add(headerLabel);
        
        add(headerPanel);
        add(Box.createRigidArea(new Dimension(0, SPACING_MEDIUM)));
        
        // Subtotal row
        subtotalLabel = new JLabel();
        subtotalLabel.setFont(FONT_BODY);
        add(createSummaryRow("Subtotal:", subtotalLabel));
        add(Box.createRigidArea(new Dimension(0, SPACING_SMALL)));
        
        // Shipping row
        shippingLabel = new JLabel();
        shippingLabel.setFont(FONT_BODY);
        add(createSummaryRow("Shipping Fee:", shippingLabel));
        add(Box.createRigidArea(new Dimension(0, SPACING_MEDIUM)));
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setForeground(BORDER_MEDIUM);
        add(separator);
        add(Box.createRigidArea(new Dimension(0, SPACING_MEDIUM)));
        
        // Total row (highlighted)
        totalLabel = new JLabel();
        totalLabel.setFont(FONT_TITLE);
        totalLabel.setForeground(SUCCESS_COLOR);
        JPanel totalRow = createSummaryRow("Total:", totalLabel);
        totalRow.setBackground(new Color(240, 255, 240));
        totalRow.setOpaque(true);
        totalRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SUCCESS_COLOR, 2),
            BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_MEDIUM, SPACING_SMALL, SPACING_MEDIUM)
        ));
        add(totalRow);
        
        add(Box.createRigidArea(new Dimension(0, SPACING_LARGE)));
        
        add(Box.createVerticalGlue());
    }
    
    private JPanel createSummaryRow(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(SPACING_MEDIUM, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_PRIMARY);
        
        row.add(label, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        
        return row;
    }
    
    private void updateSummary() {
        subtotalLabel.setText(String.format("$%.2f", invoice.getSubtotal()));
        shippingLabel.setText(String.format("$%.2f", invoice.getShippingFee()));
        totalLabel.setText(String.format("$%.2f", invoice.getTotal()));
    }
}
