package com.aims.controller;

import com.aims.entity.cart.Cart;
import com.aims.entity.cart.CartMedia;
import com.aims.entity.invoice.Invoice;
import com.aims.entity.order.Order;
import com.aims.entity.order.OrderMedia;

import java.sql.SQLException;

public class PlaceOrderController extends BaseController{
    public void placeOrder() throws SQLException{
        Cart.getCart().checkAvailabilityOfProduct();
    }

    public Order createOrder() throws SQLException{
        Order order = new Order();
        for (Object object : Cart.getCart().getListMedia()) {
            CartMedia cartMedia = (CartMedia) object;
            OrderMedia orderMedia = new OrderMedia(cartMedia.getMedia(),
                                                   cartMedia.getQuantity(),
                                                   cartMedia.getPrice());
            order.addOrderMedia(orderMedia);
        }
        return order;
    }

    public Invoice createInvoice(Order order) {
        return new Invoice(order);
    }
}
