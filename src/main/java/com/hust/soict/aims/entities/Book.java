package com.hust.soict.aims.entities;

public class Book extends Product {
    private String author;
    private String coverType; // paperback or hardcover
    private String publisher;
    private String publicationDate; // publish_date in DB
    private Integer numberOfPages; // number_of_page in DB
    private String language; // optional
    private String bookCategory; // book_category in DB
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
    public String getBookCategory() { return bookCategory; }
    public void setBookCategory(String bookCategory) { this.bookCategory = bookCategory; }

    @Override
    public String getType() { 
        if (super.getCategory() != null) return super.getCategory().toLowerCase();
        return "book"; 
    }
}
