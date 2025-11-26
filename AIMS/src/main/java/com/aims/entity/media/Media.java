package com.aims.entity.media;

import java.sql.SQLException;
import java.sql.Statement;

public class Media {
    protected Statement stm;
    protected int id;
    protected String title;
    protected String category;
    protected int price;
    protected int quantity;
    protected String imageURL;
    protected String description;
    protected double weight;

    public Media() throws SQLException { // báo lỗi khi làm vc với Db
    }

    public Media(int id, String title, String category, int price, int quantity, String imageURL, String description,
            double weight) throws SQLException {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.imageURL = imageURL;
        this.description = description;
        this.weight = weight;
    }

    public Media(int id, String title, String category, int price, int quantity) throws SQLException {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return this.quantity;
    }

    // getter and setter
    public int getId() {
        return this.id;
    }

    protected Media setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Media setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getCategory() {
        return this.category;
    }

    public Media setCategory(String category) {
        this.category = category;
        return this;
    }

    public int getPrice() {
        return this.price;
    }

    public Media setPrice(int price) {
        this.price = price;
        return this;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public Media setMediaURL(String url) {
        this.imageURL = url;
        return this;
    }

    public Media setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    // Add getter and setter for description
    public String getDescription() {
        return this.description;
    }

    public Media setDescription(String description) {
        this.description = description;
        return this;
    }

    public double getWeight() {
        return weight;
    }

    public Media setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + id + "'" +
                ", title='" + title + "'" +
                ", category='" + category + "'" +
                ", price='" + price + "'" +
                ", quantity='" + quantity + "'" +
                ", imageURL='" + imageURL + "'" +
                "}";
    }

    public String getType() {
        return "";
    }

}