package com.aims.controller;

import com.aims.dao.MediaDao;
import com.aims.entity.cart.Cart;
import com.aims.entity.cart.CartMedia;

import java.sql.SQLException;
import java.util.List;

public class HomeController extends BaseController{
    public CartMedia checkMediaInCart(int id){
        return Cart.getCart().checkMediaInCart(id);
    }
    public List getAllMedia() throws SQLException{
        return new MediaDao().getAll();
    }

}