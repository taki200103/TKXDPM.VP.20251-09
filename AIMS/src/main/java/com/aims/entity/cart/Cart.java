package com.aims.entity.cart;

import com.aims.exception.MediaNotAvailableException;   

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<CartMedia> listCartMedia;
    private static Cart cartInstance;

    public static Cart getCart(){
        if(cartInstance == null) cartInstance = new Cart();
        return cartInstance;
    }

    private Cart(){
        listCartMedia = new ArrayList<>();
    }

    public void addCartMedia(CartMedia cm){
        listCartMedia.add(cm);
    }

    public void removeCartMedia(CartMedia cm){
        listCartMedia.remove(cm);
    }

    public List<CartMedia> getListMedia(){  // Return the mutable list directly
        return List.copyOf(listCartMedia);
    }

    public void emptyCart(){
        listCartMedia.clear();
    }

    public int calSubtotal(){
        int total = 0;
        for (CartMedia obj : listCartMedia) {
            total += obj.getPrice()* obj.getQuantity();
        }
        return total;
    }

    public void checkAvailabilityOfProduct() throws SQLException{
        boolean allAvai = true;
        for (CartMedia object : listCartMedia) {
            int requiredQuantity = object.getQuantity();
            int availQuantity = object.getMedia().getQuantity();
            if (requiredQuantity > availQuantity) allAvai = false;
        }
        if (!allAvai) throw new MediaNotAvailableException("Some media not available");
    }

    public CartMedia checkMediaInCart(int id){
        for (CartMedia cartMedia : listCartMedia) {
            if (cartMedia.getMedia().getId() == id) return cartMedia;
        }
        return null;
    }

    public int getCartSize() {
        return listCartMedia.size();
    }
}
