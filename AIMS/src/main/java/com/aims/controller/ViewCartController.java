package com.aims.controller;

import com.aims.entity.cart.Cart;

import java.sql.SQLException;

public class ViewCartController extends BaseController{
    public void checkAvailabilityOfProduct() throws SQLException{
        Cart.getCart().checkAvailabilityOfProduct();
    }

    public int getCartSubtotal(){
        int subtotal = Cart.getCart().calSubtotal();
        return subtotal;
    }

}
