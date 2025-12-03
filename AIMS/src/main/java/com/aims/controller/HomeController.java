package com.aims.controller;

import com.aims.dao.MediaDao;
import com.aims.entity.cart.Cart;
import com.aims.entity.cart.CartMedia;
import com.aims.entity.media.Media;

import java.util.ArrayList;
import java.util.List;

public class HomeController extends BaseController {

    public CartMedia checkMediaInCart(int id){
        return Cart.getCart().checkMediaInCart(id);
    }

    public List getAllMedia() {
        // Tạo danh sách giả để không phải gọi Database
        List<Media> list = new ArrayList<>();

        try {
            // SỬA LỖI: Chỉ dùng đúng 5 tham số (id, title, category, price, quantity)
            // Và KHÔNG gọi hàm setImageURL nữa để tránh lỗi

            // Sản phẩm 1
            Media m1 = new Media(1, "Lap trinh Java", "Book", 120000, 10);
            list.add(m1);

            // Sản phẩm 2
            Media m2 = new Media(2, "Doraemon Vol.1", "Book", 25000, 100);
            list.add(m2);

            // Sản phẩm 3
            Media m3 = new Media(3, "Tom and Jerry", "DVD", 55000, 5);
            list.add(m3);

            // Sản phẩm 4
            Media m4 = new Media(4, "Nhac tre remix", "CD", 30000, 20);
            list.add(m4);

            // Sản phẩm 5
            Media m5 = new Media(5, "Clean Code", "Book", 350000, 2);
            list.add(m5);

            // Sản phẩm 6
            Media m6 = new Media(6, "Design Patterns", "Book", 400000, 50);
            list.add(m6);

            // Sản phẩm 7
            Media m7 = new Media(7, "Avengers: Endgame", "DVD", 200000, 15);
            list.add(m7);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}