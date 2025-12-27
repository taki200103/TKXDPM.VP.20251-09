package com.hust.soict.aims;

import javax.swing.*;
import org.springframework.context.ConfigurableApplicationContext;

import com.hust.soict.aims.utils.ServiceProvider;
import com.hust.soict.aims.controls.PayByCreditCardController;
import com.hust.soict.aims.controls.ProductController;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.boundaries.customer.homepage.Homepage;
import com.hust.soict.aims.boundaries.ScreenNavigator;

public class App {
    public static void main(String[] args) {
        // Disable headless mode to allow Swing GUI to work
        System.setProperty("java.awt.headless", "false");

        // Start embedded Spring Boot for PayPal callbacks
        ConfigurableApplicationContext ctx = EmbeddedTomcat.startAndGetContext(new String[]{});
        PayByCreditCardController paymentController = ctx.getBean(PayByCreditCardController.class);

        // Initialize ServiceProvider with payment controller
        // Now other classes can get it via ServiceProvider.getInstance()
        ServiceProvider.getInstance().initialize(paymentController);

        SwingUtilities.invokeLater(() -> {
            ProductController productController = new ProductController();
            CartController cartController = new CartController();

            Homepage homepage = new Homepage(productController, cartController);

            ScreenNavigator.getInstance().navigateTo(homepage);
        });
    }
}