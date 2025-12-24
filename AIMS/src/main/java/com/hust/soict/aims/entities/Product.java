package com.hust.soict.aims.entities;

import java.sql.Timestamp;

public class Product {
    protected long id;
    protected String category; // Book | Newspaper | CD | DVD
    protected String title;
    protected double originalValue; // value in DB
    protected double currentPrice; // price in DB
    protected double weight;
    protected String dimension; // Legacy field, computed from height/width/length
    protected Double height;
    protected Double width;
    protected Double length;
    protected String description;
    protected String barcode;
    protected String imagePath; // image_url in DB
    protected int quantity; // stock/quantity
    protected String condition; // new | used
    protected String status; // active | deactivated
    protected Integer createdBy;
    protected Integer updatedBy;
    protected Timestamp createdAt;
    protected Timestamp updatedAt;

    public Product() {
        this.condition = "new";
        this.status = "active";
        this.quantity = 0;
    }

    public Product(long id, String title, double originalValue, double currentPrice, double weight, String dimension, String description) {
        this();
        this.id = id;
        this.title = title;
        this.originalValue = originalValue;
        this.currentPrice = currentPrice;
        this.weight = weight;
        this.dimension = dimension;
        this.description = description;
        this.barcode = String.valueOf(id);
        this.imagePath = null;
    }
    
    public Product(long id, String title, double originalValue, double currentPrice, double weight, String dimension, String description, String barcode, String imagePath) {
        this();
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
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getOriginalValue() { return originalValue; }
    public void setOriginalValue(double originalValue) { this.originalValue = originalValue; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public String getDimension() { 
        // Compute dimension from height/width if available
        if (dimension != null && !dimension.isEmpty()) {
            return dimension;
        }
        if (width != null && height != null) {
            return width.intValue() + "x" + height.intValue() + "cm";
        }
        return dimension;
    }
    public void setDimension(String dimension) { 
        this.dimension = dimension;
        // Try to parse dimension to width/height
        if (dimension != null && !dimension.isEmpty()) {
            String[] parts = dimension.replaceAll("[^0-9xX.]", "").split("[xX]");
            if (parts.length >= 2) {
                try {
                    this.width = Double.parseDouble(parts[0]);
                    this.height = Double.parseDouble(parts[1]);
                } catch (NumberFormatException ignored) {}
            }
        }
    }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }
    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getType() { 
        return category != null ? category.toLowerCase() : "product"; 
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f)", title, currentPrice);
    }
}
