package com.hust.soict.aims.entities;

public class Product {
    protected long id;
    protected String title;
    protected double originalValue;
    protected double currentPrice;
    protected double weight;
    protected String dimension;
    protected String description;
    protected String barcode;
    protected String imagePath;

    public Product() {}

    public Product(long id, String title, double originalValue, double currentPrice, double weight, String dimension, String description) {
        this.id = id;
        this.title = title;
        this.originalValue = originalValue;
        this.currentPrice = currentPrice;
        this.weight = weight;
        this.dimension = dimension;
        this.description = description;
        this.barcode = String.valueOf(id); // Default barcode is the product ID
        this.imagePath = null;
    }
    
    public Product(long id, String title, double originalValue, double currentPrice, double weight, String dimension, String description, String barcode, String imagePath) {
        this.id = id;
        this.title = title;
        this.originalValue = originalValue;
        this.currentPrice = currentPrice;
        this.weight = weight;
        this.dimension = dimension;
        this.description = description;
        this.barcode = barcode != null ? barcode : String.valueOf(id);
        this.imagePath = imagePath;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getOriginalValue() { return originalValue; }
    public void setOriginalValue(double originalValue) { this.originalValue = originalValue; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getType() { return "product"; }

    @Override
    public String toString() {
        return String.format("%s (%.2f)", title, currentPrice);
    }
}
