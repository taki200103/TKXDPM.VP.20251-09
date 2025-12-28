package com.hust.soict.aims.entities;

public class Newspaper extends Product {
    private String editorInChief;
    private String publisher;
    private String publicationDate;
    private String issueNumber; // additional
    private String publicationFrequency; // additional
    private String issn; // additional
    private String language; // additional
    private String sections; // additional (comma separated)

    public Newspaper() {}

    public Newspaper(long id, String title, double originalValue, double currentPrice, double weight, String dimension, String description,
                     String editorInChief, String publisher, String publicationDate) {
        super(id, title, originalValue, currentPrice, weight, dimension, description);
        this.editorInChief = editorInChief;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
    }

    public String getEditorInChief() { return editorInChief; }
    public void setEditorInChief(String editorInChief) { this.editorInChief = editorInChief; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getPublicationDate() { return publicationDate; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }
    public String getIssueNumber() { return issueNumber; }
    public void setIssueNumber(String issueNumber) { this.issueNumber = issueNumber; }
    public String getPublicationFrequency() { return publicationFrequency; }
    public void setPublicationFrequency(String publicationFrequency) { this.publicationFrequency = publicationFrequency; }
    public String getIssn() { return issn; }
    public void setIssn(String issn) { this.issn = issn; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getSections() { return sections; }
    public void setSections(String sections) { this.sections = sections; }

    @Override
    public String getType() { return "newspaper"; }
}
