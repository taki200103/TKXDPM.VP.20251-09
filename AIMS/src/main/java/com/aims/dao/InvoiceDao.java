package com.aims.dao;

import com.aims.entity.db.DBconnection;
import com.aims.entity.invoice.Invoice;
import com.aims.entity.order.Order;
import com.aims.entity.payment.PaymentTransaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class InvoiceDao implements Dao<Invoice>{

    @Override
    public List<Invoice> getAll() throws SQLException {
        String query = "SELECT invoice_id, order_id, payment_transaction_id, total_amount FROM Invoice";
        Connection conn = DBconnection.getConnection();
        List<Invoice> invoices = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Optional<Order> optionalOrder = new OrderDao().get(rs.getInt("order_id"));
                Order order = optionalOrder.orElse(new Order());
                Invoice invoice = new Invoice(order);
                invoice.setId(rs.getInt("invoice_id"));
                Optional<PaymentTransaction> optionalPaymentTransaction = new PaymentDao().get(rs.getInt("payment_transaction_id"));
                PaymentTransaction paymentTransaction = optionalPaymentTransaction.orElse(new PaymentTransaction());
                invoice.setPaymentTransaction(paymentTransaction);

                invoices.add(invoice);
            }
        }

        return invoices;
    }


    @Override
    public Optional<Invoice> get(int id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public void save(Invoice invoice) throws SQLException {

    }

    @Override
    public void update(Invoice invoice) throws SQLException {

    }

    @Override
    public void delete(Invoice invoice) {

    }
}
