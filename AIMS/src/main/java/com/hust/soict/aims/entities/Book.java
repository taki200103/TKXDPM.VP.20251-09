package com.hust.soict.aims.entities;

public class Book extends Product {
    private String author;
    private String coverType; // paperback or hardcover
    private String publisher;
    private String publicationDate;
    private Integer numberOfPages; // optional
    private String language; // optional
    private String genre; // optional

    public Book() {}

    public Book(long id, String title, double originalValue, double currentPrice, double weight, String dimension, String description,
                String author, String coverType, String publisher, String publicationDate) {
        super(id, title, originalValue, currentPrice, weight, dimension, description);
        this.author = author;
        this.coverType = coverType;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCoverType() { return coverType; }
    public void setCoverType(String coverType) { this.coverType = coverType; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getPublicationDate() { return publicationDate; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }
    public Integer getNumberOfPages() { return numberOfPages; }
    public void setNumberOfPages(Integer numberOfPages) { this.numberOfPages = numberOfPages; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    @Override
    public String getType() { return "book"; }
}
