package com.aims.controller;

import com.aims.entity.cart.Cart;
import com.aims.entity.cart.CartMedia;
import com.aims.entity.media.Media;
import com.aims.entity.order.Order;
import com.aims.entity.order.OrderMedia;
import com.aims.exception.MediaNotAvailableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlaceOrderTest {
    private PlaceOrderController controller;
    private Cart cart;

    @BeforeEach
    void setUp() {
        controller = new PlaceOrderController();
        cart = Cart.getCart();
        cart.emptyCart();
    }

    @AfterEach
    void tearDown() {
        cart.emptyCart();
    }

    @Test
    void createOrderCopiesCartContents() throws SQLException {
        Media media = new Media(1, "Book 1", "Book", 150000, 5);
        cart.addCartMedia(new CartMedia(media, 2, 140000));

        Order order = controller.createOrder();

        assertEquals(1, order.getlistOrderMedia().size());
        OrderMedia orderMedia = order.getlistOrderMedia().get(0);
        assertEquals(media, orderMedia.getMedia());
        assertEquals(2, orderMedia.getQuantity());
        assertEquals(140000, orderMedia.getPrice());
    }

    @Test
    void placeOrderThrowsWhenStockInsufficient() throws SQLException {
        Media media = new Media(2, "Book 2", "Book", 150000, 1);
        cart.addCartMedia(new CartMedia(media, 2, 150000));

        assertThrows(MediaNotAvailableException.class, () -> controller.placeOrder());
    }
}

