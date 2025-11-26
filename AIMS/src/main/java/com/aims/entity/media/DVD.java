package com.aims.entity.media;

import java.sql.SQLException;

public class DVD extends Media {

    String discType;
    String director;
    int runtime;
    String studio;
    String subtitles;
    String releaseDate;

    public DVD() throws SQLException {

    }

    public DVD(int id, String title, String category, int price, int quantity, String imageUrl, String description,
            double weight, String discType,
            String director, int runtime, String studio, String subtitles, String releaseDate) throws SQLException {
        super(id, title, category, price, quantity, imageUrl, description, weight);
        this.discType = discType;
        this.director = director;
        this.runtime = runtime;
        this.studio = studio;
        this.subtitles = subtitles;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return this.id;
    }

    public DVD setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public DVD setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getCategory() {
        return this.category;
    }

    public DVD setCategory(String category) {
        this.category = category;
        return this;
    }

    public int getPrice() {
        return this.price;
    }

    public DVD setPrice(int price) {
        this.price = price;
        return this;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public DVD setMediaURL(String url) {
        this.imageURL = url;
        return this;
    }

    public DVD setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    // Add getter and setter for description
    public String getDescription() {
        return this.description;
    }

    public DVD setDescription(String description) {
        this.description = description;
        return this;
    }

    public double getWeight() {
        return weight;
    }

    public DVD setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public DVD setDiscType(String discType) {
        this.discType = discType;
        return this;
    }

    public String getDirector() {
        return this.director;
    }

    public DVD setDirector(String director) {
        this.director = director;
        return this;
    }

    public int getRuntime() {
        return this.runtime;
    }

    public DVD setRuntime(int runtime) {
        this.runtime = runtime;
        return this;
    }

    public String getStudio() {
        return this.studio;
    }

    public DVD setStudio(String studio) {
        this.studio = studio;
        return this;
    }

    public String getSubtitles() {
        return this.subtitles;
    }

    public DVD setSubtitle(String subtitles) {
        this.subtitles = subtitles;
        return this;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public DVD setReleaseDate(String releasedDate) {
        this.releaseDate = releasedDate;
        return this;
    }

    @Override
    public String toString() {
        return "{" + super.toString() + " discType='" + discType + "'" + ", director='" + director + "'" + ", runtime='"
                + runtime + "'" + ", studio='" + studio + "'" + ", subtitles='" + subtitles + "'" + ", releasedDate='"
                + releaseDate + "'" + "}";
    }

    @Override
    public String getType() {
        return "DVD";
    }
}
