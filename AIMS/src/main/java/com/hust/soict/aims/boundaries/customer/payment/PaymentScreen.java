package com.hust.soict.aims.boundaries.customer.payment;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.hust.soict.aims.subsystems.payment.PaymentProviderRegistry;
import com.hust.soict.aims.subsystems.payment.PaymentRegistryFactory;
import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.components.RoundedButton;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.controls.PayOrderController;
import com.hust.soict.aims.controls.PlaceOrderController;
import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.entities.PaymentResult;
import com.hust.soict.aims.entities.QRCode;
import com.hust.soict.aims.entities.enums.PaymentMethod;
import com.hust.soict.aims.services.OrderService;
import com.hust.soict.aims.services.PaymentContextService;

import static com.hust.soict.aims.utils.UIConstant.*;

public class PaymentScreen extends BaseScreenHandler {

    private final Invoice invoice;
    private final PayOrderController payOrderController;
    private final OrderService orderService;
    private final PaymentContextService paymentContextService;

    private PaymentMethod currentMethod = PaymentMethod.VIETQR;
    private QRCode currentQRCode;              // Store current QR code for payment processing
    private String currentPayPalOrderId;       // Store PayPal order ID (token)

    // UI
    private JLabel qrLabel;
    private Timer paypalPollTimer;

    private RoundedButton vietQRBtn;
    private RoundedButton paypalBtn;
    private RoundedButton regenerateBtn;
    private RoundedButton paymentDoneBtn;

    private JLabel amountLabel;
    private JLabel bankLabel;
    private JLabel bankCodeLabel;
    private JLabel accountLabel;
    private JTextArea qrLinkArea;

    public PaymentScreen(BaseScreenHandler parent,
                         Invoice invoice,
                         PlaceOrderController placeOrderController) {
        super("Payment", parent, false);
        this.invoice = invoice;

        PaymentProviderRegistry registry = PaymentRegistryFactory.createDefaultRegistry();
        this.payOrderController = new PayOrderController(placeOrderController, registry);

        this.orderService = new OrderService();
        this.paymentContextService = PaymentContextService.getInstance();
        initializeScreen();
    }


    // ================= PAYPAL POLLING =================

    private void startPayPalStatusPolling(String orderId) {
        stopPayPalStatusPolling();

        paypalPollTimer = new Timer(1500, e -> {
            try {
                String status = fetchPayPalStatus(orderId);

                if ("COMPLETED".equalsIgnoreCase(status)) {
                    stopPayPalStatusPolling();

                    JOptionPane.showMessageDialog(
                            this,
                            "PayPal payment confirmed!\nOrder is processed successfully.",
                            "Payment Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    CartController.getInstance().clear();
                    navigateHomeAndClearStack();

                } else if ("FAILED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                    stopPayPalStatusPolling();

                    JOptionPane.showMessageDialog(
                            this,
                            "PayPal payment was not completed (" + status + ").\nPlease try again.",
                            "Payment",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            } catch (Exception ignore) {
                // If server not ready / temporarily unreachable, ignore and keep polling
            }
        });

        paypalPollTimer.start();
    }

    private String fetchPayPalStatus(String orderId) throws Exception {
        URL url = new URL("http://localhost:8080/paypal-status?orderId=" + orderId);
        try (var in = url.openStream()) {
            return new String(in.readAllBytes()).trim();
        }
    }

    private void stopPayPalStatusPolling() {
        if (paypalPollTimer != null) {
            paypalPollTimer.stop();
            paypalPollTimer = null;
        }
    }

    // ================= INIT =================

    @Override
    protected void initComponents() {

        qrLabel = new JLabel("", SwingConstants.CENTER);
        qrLabel.setPreferredSize(new Dimension(320, 320));
        qrLabel.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT));

        vietQRBtn = new RoundedButton("VietQR");
        paypalBtn = new RoundedButton("PayPal");

        vietQRBtn.setFont(FONT_BUTTON);
        vietQRBtn.setBackground(VIETQR_COLOR);
        vietQRBtn.setForeground(TEXT_ON_PRIMARY);
        vietQRBtn.setCursor(CURSOR_HAND);
        vietQRBtn.setPreferredSize(new Dimension(180, 40));

        paypalBtn.setFont(FONT_BUTTON);
        paypalBtn.setBackground(PAYPAL_COLOR);
        paypalBtn.setForeground(TEXT_ON_PRIMARY);
        paypalBtn.setCursor(CURSOR_HAND);
        paypalBtn.setPreferredSize(new Dimension(180, 40));

        regenerateBtn = new RoundedButton("Regenerate Payment");
        regenerateBtn.setFont(FONT_BUTTON);
        regenerateBtn.setBackground(PRIMARY_COLOR);
        regenerateBtn.setForeground(TEXT_ON_PRIMARY);
        regenerateBtn.setCursor(CURSOR_HAND);
        regenerateBtn.setPreferredSize(new Dimension(200, 40));

        // Payment done button (ONLY for VietQR demo)
        paymentDoneBtn = new RoundedButton("Payment Successful");
        paymentDoneBtn.setFont(FONT_BUTTON);
        paymentDoneBtn.setBackground(SUCCESS_COLOR);
        paymentDoneBtn.setForeground(TEXT_ON_PRIMARY);
        paymentDoneBtn.setCursor(CURSOR_HAND);
        paymentDoneBtn.setPreferredSize(new Dimension(180, 40));

        amountLabel = new JLabel();
        bankLabel = new JLabel();
        bankCodeLabel = new JLabel();
        accountLabel = new JLabel();

        amountLabel.setFont(FONT_PRODUCT_NAME);
        bankLabel.setFont(FONT_PRODUCT_NAME);
        bankCodeLabel.setFont(FONT_PRODUCT_NAME);
        accountLabel.setFont(FONT_PRODUCT_NAME);

        qrLinkArea = new JTextArea(3, 40);
        qrLinkArea.setEditable(false);
        qrLinkArea.setLineWrap(true);
        qrLinkArea.setWrapStyleWord(true);
        qrLinkArea.setFont(FONT_SMALL);
    }

    // ================= LAYOUT =================

    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(PADDING_MEDIUM);

        JLabel title = new JLabel("Payment");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_ON_PRIMARY);
        header.add(title, BorderLayout.CENTER);

        add(createHeaderWithNavigation(header), BorderLayout.NORTH);

        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        methodPanel.setBorder(BorderFactory.createTitledBorder("Payment Method"));
        methodPanel.add(vietQRBtn);
        methodPanel.add(paypalBtn);

        JPanel qrPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        qrPanel.add(qrLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Payment Information"));

        infoPanel.add(amountLabel);
        infoPanel.add(bankLabel);
        infoPanel.add(bankCodeLabel);
        infoPanel.add(accountLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(new JScrollPane(qrLinkArea));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(PADDING_MEDIUM);

        center.add(methodPanel);
        center.add(Box.createVerticalStrut(10));
        center.add(qrPanel);
        center.add(Box.createVerticalStrut(10));
        center.add(infoPanel);

        add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.add(paymentDoneBtn);
        footer.add(regenerateBtn);
        add(footer, BorderLayout.SOUTH);
    }

    // ================= EVENTS =================

    @Override
    protected void bindEvents() {
        vietQRBtn.addActionListener(e -> switchMethod(PaymentMethod.VIETQR));
        paypalBtn.addActionListener(e -> switchMethod(PaymentMethod.PAYPAL));
        regenerateBtn.addActionListener(e -> generatePayment());

        // Only used for VietQR demo
        paymentDoneBtn.addActionListener(e -> onPaymentSuccess());
    }

    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        switchMethod(PaymentMethod.VIETQR);
    }

    // ================= CORE LOGIC =================

    private void switchMethod(PaymentMethod method) {
        currentMethod = method;
        updateMethodButtonStyle();
        updatePaymentDoneButtonState();
        clearPaymentInformation();
        generatePayment();
    }

    private void updatePaymentDoneButtonState() {
        if (currentMethod == PaymentMethod.PAYPAL) {
            paymentDoneBtn.setEnabled(false);
            paymentDoneBtn.setText("Pay on PayPal (auto confirm)");
            paymentDoneBtn.setToolTipText("Complete payment in browser. System confirms automatically.");
        } else {
            paymentDoneBtn.setEnabled(true);
            paymentDoneBtn.setText("Payment Successful");
            paymentDoneBtn.setToolTipText("VietQR demo: click after transfer.");
        }
    }

    private void clearPaymentInformation() {
        qrLabel.setIcon(null);
        qrLabel.setText("");

        amountLabel.setText("");
        bankLabel.setText("");
        bankCodeLabel.setText("");
        accountLabel.setText("");
        qrLinkArea.setText("");
    }

    private void updateMethodButtonStyle() {
        if (currentMethod == PaymentMethod.VIETQR) {
            vietQRBtn.setBackground(PRIMARY_COLOR);
            paypalBtn.setBackground(PAYPAL_COLOR);
        } else {
            paypalBtn.setBackground(PRIMARY_COLOR);
            vietQRBtn.setBackground(VIETQR_COLOR);
        }
    }

    private void generatePayment() {
        qrLabel.setText("Generating payment...");

        SwingWorker<PaymentResult, Void> worker = new SwingWorker<>() {
            @Override
            protected PaymentResult doInBackground() throws Exception {
                return payOrderController.createPayment(invoice, currentMethod);
            }

            @Override
            protected void done() {
                try {
                    handlePaymentResult(get());
                } catch (Exception e) {
                    e.printStackTrace();
                    qrLabel.setText("Payment failed");
                }
            }
        };
        worker.execute();
    }

    private void handlePaymentResult(PaymentResult result) {
        if (result.getMethod() == PaymentMethod.VIETQR) {
            displayVietQR(result.getQrCode());
            return;
        }

        // PAYPAL
        String payUrl = result.getPayUrl();
        this.currentPayPalOrderId = extractPayPalOrderIdFromUrl(payUrl);

        if (this.currentPayPalOrderId != null) {
            // Store mapping using REAL orderId (token) so callback can find invoice
            paymentContextService.storePayPalOrder(this.currentPayPalOrderId, invoice);
        } else {
            System.err.println("[PaymentScreen] Cannot extract PayPal orderId from approve URL: " + payUrl);
        }

        displayPayPalInfo(payUrl);
        redirectToPayPal(payUrl);

        // Start polling so screen auto closes when callback success
        if (this.currentPayPalOrderId != null) {
            startPayPalStatusPolling(this.currentPayPalOrderId);
        }
    }

    private String extractPayPalOrderIdFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery(); // token=XXXX...
            if (query == null) return null;

            for (String part : query.split("&")) {
                String[] kv = part.split("=", 2);
                if (kv.length == 2 && "token".equalsIgnoreCase(kv[0])) {
                    return kv[1];
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    // ================= DISPLAY =================

    private void displayVietQR(QRCode qr) {
        this.currentQRCode = qr;

        try {
            BufferedImage img = ImageIO.read(new URL(qr.getQrCode()));
            Image scaled = img.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            qrLabel.setIcon(new ImageIcon(scaled));
            qrLabel.setText("");
        } catch (Exception e) {
            qrLabel.setText("Cannot load QR");
        }

        amountLabel.setText(String.format("Amount: %.0f VND", invoice.getTotalAmount()));
        bankLabel.setText("Bank: " + qr.getBankName());
        bankCodeLabel.setText("Bank Code: " + qr.getBankCode());
        accountLabel.setText("Account: " + qr.getBankAccount());
        qrLinkArea.setText(qr.getQrLink());
    }

    private void displayPayPalInfo(String url) {
        qrLabel.setText("<html><center><h3>Redirecting to PayPal...</h3>" +
                "<div>Complete payment in browser.<br/>System will confirm automatically.</div></center></html>");
        amountLabel.setText(String.format("Amount: %.0f VND", invoice.getTotalAmount()));

        bankLabel.setText("");
        bankCodeLabel.setText("");
        accountLabel.setText("");

        qrLinkArea.setText(url);
    }

    private void redirectToPayPal(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Desktop browse not supported.\nOpen this URL manually:\n" + url,
                        "PayPal",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot open browser.\n" + url,
                    "PayPal",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // ================= SUCCESS FLOW =================

    private void onPaymentSuccess() {
        // For PAYPAL, never allow manual success.
        if (currentMethod == PaymentMethod.PAYPAL) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please complete payment on PayPal in your browser.\nThe system will confirm automatically after you return.",
                    "PayPal",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // VIETQR demo success
        boolean success;
        String transactionNo = "QR_" + System.currentTimeMillis();
        String bankCode = currentQRCode != null ? currentQRCode.getBankCode() : null;
        String bankTransactionNo = transactionNo;

        success = orderService.processQRPaymentOrder(invoice, transactionNo, bankCode, bankTransactionNo);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Payment completed successfully.\nOrder has been processed.\nThank you for your purchase!",
                    "Payment Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Payment completed but there was an error processing your order.\nPlease contact support.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        CartController.getInstance().clear();
        navigateHomeAndClearStack();
    }

    private void navigateHomeAndClearStack() {
        // Stop polling if any
        stopPayPalStatusPolling();

        BaseScreenHandler root = this;
        while (root.getParentScreen() != null) {
            root = root.getParentScreen();
        }

        getNavigator().clearAndNavigateTo(root);
        dispose();
    }
}
