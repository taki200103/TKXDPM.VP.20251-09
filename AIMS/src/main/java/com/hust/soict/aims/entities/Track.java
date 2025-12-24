package com.hust.soict.aims.entities;

public class Track {
    private long trackId;
    private long mediaId; // CD media_id
    private String title;
    private Integer length; // in seconds
    private Integer trackNumber;

    public Track() {}

    public Track(long trackId, long mediaId, String title, Integer length, Integer trackNumber) {
        this.trackId = trackId;
        this.mediaId = mediaId;
        this.title = title;
        this.length = length;
        this.trackNumber = trackNumber;
    }

    public long getTrackId() { return trackId; }
    public void setTrackId(long trackId) { this.trackId = trackId; }
    public long getMediaId() { return mediaId; }
    public void setMediaId(long mediaId) { this.mediaId = mediaId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getLength() { return length; }
    public void setLength(Integer length) { this.length = length; }
    public Integer getTrackNumber() { return trackNumber; }
    public void setTrackNumber(Integer trackNumber) { this.trackNumber = trackNumber; }
}
