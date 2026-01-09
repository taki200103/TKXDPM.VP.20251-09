package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.Invoice;
import com.hust.soict.aims.services.EmailService;
import com.hust.soict.aims.services.PaymentContextService;
import com.hust.soict.aims.subsystems.paypal.PayPalSubsystemController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/paypal-success")
public class PayPalSuccessController extends HttpServlet {

    private final PayPalSubsystemController paypalCtrl = new PayPalSubsystemController();
    private final PaymentContextService ctx = PaymentContextService.getInstance();
    private final EmailService emailService = EmailService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String orderId = req.getParameter("token"); // PayPal trả token=ORDER_ID

        if (orderId == null || orderId.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing token(orderId)");
            return;
        }

        try {
            boolean ok = paypalCtrl.captureOrder(orderId);
            if (!ok) {
                resp.sendRedirect(req.getContextPath() + "/payment-failed");
                return;
            }

            // Lấy invoice đúng của USER từ mapping
            Invoice invoice = ctx.getInvoiceForPayPalOrder(orderId);
            if (invoice == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Invoice not found for PayPal order: " + orderId);
                return;
            }

            // TODO: cập nhật trạng thái invoice/order theo hệ thống của bạn
            // invoice.setPaid(true);
            // invoice.getOrder().setStatus(OrderStatus.PAID);

            String customerEmail = invoice.getOrder().getDeliveryInfo().getEmail(); // nếu có
            if (customerEmail != null && !customerEmail.isBlank()) {
                emailService.sendPaymentConfirmationEmail(invoice, customerEmail);
            }

            ctx.removePayPalOrder(orderId);

            resp.sendRedirect(req.getContextPath() + "/payment-success");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Capture failed: " + e.getMessage());
        }
    }
}
