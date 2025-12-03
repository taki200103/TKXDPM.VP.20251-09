package com.aims.entity.media;

import java.sql.SQLException;

public class CD extends Media {
    String artist;
    String recordLabel;
    String musicType;
    String releasedDate;

    public CD() throws SQLException {

    }

    public CD(int id, String title, String category, int price, int quantity, String imageUrl, String description,
            double weight, String artist,
            String recordLabel, String musicType, String releasedDate) throws SQLException {
        super(id, title, category, price, quantity, imageUrl, description, weight);
        this.artist = artist;
        this.recordLabel = recordLabel;
        this.musicType = musicType;
        this.releasedDate = releasedDate;
    }

    public int getId() {
        return this.id;
    }

    public CD setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public CD setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getCategory() {
        return this.category;
    }

    public CD setCategory(String category) {
        this.category = category;
        return this;
    }

    public int getPrice() {
        return this.price;
    }

    public CD setPrice(int price) {
        this.price = price;
        return this;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public CD setMediaURL(String url) {
        this.imageURL = url;
        return this;
    }

    public CD setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    // Add getter and setter for description
    public String getDescription() {
        return this.description;
    }

    public CD setDescription(String description) {
        this.description = description;
        return this;
    }

    public double getWeight() {
        return weight;
    }

    public CD setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public String getArtist() {
        return this.artist;
    }

    public CD setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getRecordLabel() {
        return this.recordLabel;
    }

    public CD setRecordLabel(String recordLabel) {
        this.recordLabel = recordLabel;
        return this;
    }

    public CD setMusicType(String musicType) {
        this.musicType = musicType;
        return this;
    }

    public String getMusicType() {
        return this.musicType;
    }

    public String getReleasedDate() {
        return this.releasedDate;
    }

    public CD setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
        return this;
    }

    @Override
    public String toString() {
        return "{" + super.toString() + " artist='" + artist + "'" + ", recordLabel='" + recordLabel + "'"
                + "'" + ", musicType='" + musicType + "'" + ", releasedDate='"
                + releasedDate + "'" + "}";
    }

    @Override
    public String getType() {
        return "CD";
    }
}
