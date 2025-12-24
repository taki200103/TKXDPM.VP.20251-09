package com.hust.soict.aims.entities;

import java.util.List;

public class CD extends Product {
    private String album;
    private String artist;
    private String recordLabel;
    private String genre;
    private String releaseDate; // optional
    private List<String> trackList; // optional

    public CD() {}

    public CD(long id, String title, double originalValue, double currentPrice, double weight, String dimension, String description,
              String album, String artist, String recordLabel) {
        super(id, title, originalValue, currentPrice, weight, dimension, description);
        this.album = album;
        this.artist = artist;
        this.recordLabel = recordLabel;
    }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getRecordLabel() { return recordLabel; }
    public void setRecordLabel(String recordLabel) { this.recordLabel = recordLabel; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public List<String> getTrackList() { return trackList; }
    public void setTrackList(List<String> trackList) { this.trackList = trackList; }

    @Override
    public String getType() { return "cd"; }
}
