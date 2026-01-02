package com.hust.soict.aims.utils;

import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.Order;
import com.hust.soict.aims.entities.CartItem;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.List;
import java.util.Date;
import java.util.TimeZone;

/**
 * Service for sending email notifications
 */
public class EmailService {
    private String senderEmail;
    private String senderPassword;
    private String smtpHost = "smtp.gmail.com";
    private int smtpPort = 587;

    public EmailService() {
        // Load configuration from application.properties or environment
        this.senderEmail = ConfigLoader.getProperty("email.sender", "");
        this.senderPassword = ConfigLoader.getProperty("email.password", "");
        
        String host = ConfigLoader.getProperty("email.smtp.host", smtpHost);
        if (!host.isEmpty()) {
            this.smtpHost = host;
        }
        String port = ConfigLoader.getProperty("email.smtp.port", String.valueOf(smtpPort));
        try {
            this.smtpPort = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            // Use default
        }
    }

    /**
     * Send payment confirmation email to customer
     */
    public boolean sendPaymentConfirmationEmail(Invoice invoice, String customerEmail) {
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            System.err.println("[EmailService] No customer email provided");
            return false;
        }

        if (senderEmail == null || senderEmail.isEmpty() || senderPassword == null || senderPassword.isEmpty()) {
            System.err.println("[EmailService] Email configuration not set. Please configure email.sender and email.password in application.properties");
            return false;
        }

        try {
            Order order = invoice.getOrder();
            String subject = "Payment Confirmation - Order #" + order.getOrderId();
            String body = buildPaymentConfirmationEmailBody(invoice, order);

            return sendEmail(customerEmail, subject, body);
        } catch (Exception e) {
            System.err.println("[EmailService] Error sending payment confirmation email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Format price with thousand separators (Vietnamese format: dot as separator)
     * Example: 1000000 -> "1.000.000"
     */
    private String formatPrice(double price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format((long) price);
    }

    /**
     * Build HTML email body for payment confirmation
     */
    private String buildPaymentConfirmationEmailBody(Invoice invoice, Order order) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }");
        html.append(".content { padding: 20px; background-color: #f9f9f9; }");
        html.append(".order-info { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #4CAF50; }");
        html.append(".item { padding: 10px; border-bottom: 1px solid #ddd; }");
        html.append(".total { font-size: 18px; font-weight: bold; color: #4CAF50; margin-top: 15px; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<div class='header'><h2>Payment Confirmation</h2></div>");
        html.append("<div class='content'>");
        html.append("<p>Dear ").append(escapeHtml(order.getDeliveryInfo().getRecipientName())).append(",</p>");
        html.append("<p>Thank you for your purchase! Your payment has been successfully processed.</p>");
        
        html.append("<div class='order-info'>");
        html.append("<h3>Order Details</h3>");
        html.append("<p><strong>Order ID:</strong> #").append(order.getOrderId()).append("</p>");
        String orderDate = "N/A";
        if (order.getCreatedAt() != null) {
            // Format date with Vietnam timezone (UTC+7)
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")); // Vietnam timezone (UTC+7)
            orderDate = dateFormat.format(new Date(order.getCreatedAt().getTime()));
        }
        html.append("<p><strong>Order Date:</strong> ").append(orderDate).append("</p>");
        html.append("</div>");

        html.append("<div class='order-info'>");
        html.append("<h3>Items Ordered</h3>");
        List<CartItem> items = order.getItems();
        if (items != null) {
            for (CartItem item : items) {
                html.append("<div class='item'>");
                html.append("<strong>").append(escapeHtml(item.getProduct().getTitle())).append("</strong>");
                html.append(" - Quantity: ").append(item.getQuantity());
                html.append(" - Price: ").append(formatPrice(item.getProduct().getCurrentPrice())).append(" VNĐ");
                html.append("</div>");
            }
        }
        html.append("</div>");

        html.append("<div class='order-info'>");
        html.append("<h3>Payment Summary</h3>");
        html.append("<p>Subtotal: ").append(formatPrice(invoice.getProductTotal())).append(" VNĐ</p>");
        html.append("<p>Shipping Fee: ").append(formatPrice(invoice.getShippingFee())).append(" VNĐ</p>");
        html.append("<p class='total'>Total Amount: ").append(formatPrice(invoice.getTotalAmount())).append(" VNĐ</p>");
        html.append("</div>");

        html.append("<div class='order-info'>");
        html.append("<h3>Delivery Information</h3>");
        html.append("<p><strong>Recipient:</strong> ").append(escapeHtml(order.getDeliveryInfo().getRecipientName())).append("</p>");
        html.append("<p><strong>Phone:</strong> ").append(escapeHtml(order.getDeliveryInfo().getPhoneNumber())).append("</p>");
        html.append("<p><strong>Address:</strong> ").append(escapeHtml(order.getDeliveryInfo().getDeliveryAddress())).append("</p>");
        html.append("<p><strong>City:</strong> ").append(escapeHtml(order.getDeliveryInfo().getCity())).append("</p>");
        html.append("</div>");

        html.append("<p>We will process your order and ship it to you soon.</p>");
        html.append("<p>If you have any questions, please contact our support team.</p>");
        html.append("<p>Best regards,<br>AIMS Team</p>");
        html.append("</div></div></body></html>");

        return html.toString();
    }

    /**
     * Send email using SMTP
     */
    private boolean sendEmail(String toEmail, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", String.valueOf(smtpPort));

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("[EmailService] ✅ Email sent successfully to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("[EmailService] ❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Escape HTML special characters
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}

