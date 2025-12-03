package com.aims.controller;

import com.aims.entity.cart.Cart;
import com.aims.entity.cart.CartMedia;
import com.aims.entity.media.Media;
import com.aims.exception.MediaNotAvailableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ViewCartTest {

    private ViewCartController controller;
    private Cart cart;

    @BeforeEach
    void setUp() {
        controller = new ViewCartController();
        cart = Cart.getCart();
        cart.emptyCart();
    }

    @AfterEach
    void tearDown() {
        cart.emptyCart();
    }

    @Test
    void getCartSubtotalShouldSumAllCartItems() throws SQLException {
        Media media1 = new Media(1, "Item 1", "Book", 100_000, 10);
        Media media2 = new Media(2, "Item 2", "DVD", 200_000, 5);

        cart.addCartMedia(new CartMedia(media1, 2, 90_000));   // 180_000
        cart.addCartMedia(new CartMedia(media2, 1, 190_000));  // 190_000

        int subtotal = controller.getCartSubtotal();

        assertEquals(370_000, subtotal);
    }

    @Test
    void checkAvailabilityOfProductShouldNotThrowWhenEnoughStock() throws SQLException {
        Media media = new Media(3, "Available item", "CD", 50_000, 5);
        cart.addCartMedia(new CartMedia(media, 3, 50_000)); // yêu cầu 3, tồn kho 5 -> đủ

        controller.checkAvailabilityOfProduct(); // không ném exception
    }

    @Test
    void checkAvailabilityOfProductShouldThrowWhenInsufficientStock() throws SQLException {
        Media media = new Media(4, "Insufficient item", "Book", 80_000, 1);
        cart.addCartMedia(new CartMedia(media, 2, 80_000)); // yêu cầu 2, tồn kho 1 -> không đủ

        assertThrows(MediaNotAvailableException.class,
                () -> controller.checkAvailabilityOfProduct());
    }
}
