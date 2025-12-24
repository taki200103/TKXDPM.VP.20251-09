package com.hust.soict.aims.entities;

public class DVD extends Product {
    private String discType; // Blu-ray, HD-DVD
    private String director;
    private String runtime;
    private String studio;
    private String language;
    private String subtitles;
    private String releaseDate;
    private String genre;

    public DVD() {}

    public DVD(long id, String title, double originalValue, double currentPrice, double weight, String dimension, String description,
               String discType, String director) {
        super(id, title, originalValue, currentPrice, weight, dimension, description);
        this.discType = discType;
        this.director = director;
    }

    public String getDiscType() { return discType; }
    public void setDiscType(String discType) { this.discType = discType; }
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    public String getRuntime() { return runtime; }
    public void setRuntime(String runtime) { this.runtime = runtime; }
    public String getStudio() { return studio; }
    public void setStudio(String studio) { this.studio = studio; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getSubtitles() { return subtitles; }
    public void setSubtitles(String subtitles) { this.subtitles = subtitles; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    @Override
    public String getType() { return "dvd"; }
}
