package com.hust.soict.aims.controls.strategies;

import com.hust.soict.aims.entities.CD;
import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.entities.Track;
import com.hust.soict.aims.boundaries.ProductDetailScreen;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static com.hust.soict.aims.utils.UIConstant.*;

/**
 * Strategy Pattern - Concrete Strategy for CD products
 * 
 * This class implements the ProductDetailLoader interface specifically for CD products.
 * It knows how to:
 * 1. Load additional CD-specific data from the CD table (JOIN with Media)
 * 2. Load Track information from the Track table (JOIN with CD)
 * 3. Display all CD-related information including track list in the UI
 */
public class CDDetailLoader implements ProductDetailLoader {
    
    @Override
    public Product loadDetails(Connection conn, Product product) {
        if (!(product instanceof CD)) {
            return product;
        }
        
        CD cd = (CD) product;
        long mediaId = cd.getId();
        
        // Strategy Pattern: Load CD-specific details by joining CD table with Media
        String sql = "SELECT artist, record_label, music_type, release_date, genre " +
                     "FROM CD WHERE media_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cd.setArtist(rs.getString("artist"));
                    cd.setRecordLabel(rs.getString("record_label"));
                    cd.setAlbum(rs.getString("music_type")); // album maps to music_type in DB
                    cd.setReleaseDate(rs.getString("release_date"));
                    cd.setGenre(rs.getString("genre"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading CD details: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Strategy Pattern: Load Track information by joining Track table with CD
        List<Track> tracks = loadTracks(conn, mediaId);
        if (tracks != null && !tracks.isEmpty()) {
            // Convert Track objects to String list for backward compatibility
            List<String> trackList = new ArrayList<>();
            for (Track track : tracks) {
                String trackInfo = track.getTitle();
                if (track.getLength() != null) {
                    int minutes = track.getLength() / 60;
                    int seconds = track.getLength() % 60;
                    trackInfo += String.format(" (%d:%02d)", minutes, seconds);
                }
                trackList.add(trackInfo);
            }
            cd.setTrackList(trackList);
        }
        
        return cd;
    }
    
    /**
     * Load tracks from Track table for a CD
     * Strategy Pattern: This method performs a JOIN with Track table
     */
    private List<Track> loadTracks(Connection conn, long mediaId) {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT track_id, title, length, track_number " +
                     "FROM Track WHERE media_id = ? ORDER BY track_number ASC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Track track = new Track();
                    track.setTrackId(rs.getLong("track_id"));
                    track.setMediaId(mediaId);
                    track.setTitle(rs.getString("title"));
                    track.setLength(rs.getObject("length") != null ? rs.getInt("length") : null);
                    track.setTrackNumber(rs.getObject("track_number") != null ? 
                                        rs.getInt("track_number") : null);
                    tracks.add(track);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading tracks: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tracks;
    }
    
    @Override
    public void displayDetails(JPanel detailPanel, Product product) {
        if (!(product instanceof CD)) {
            return;
        }
        
        CD cd = (CD) product;
        
        // Strategy Pattern: Display all CD-specific information
        ProductDetailScreen.addDetailRow(detailPanel, "Album:", cd.getAlbum());
        ProductDetailScreen.addDetailRow(detailPanel, "Artist:", cd.getArtist());
        ProductDetailScreen.addDetailRow(detailPanel, "Record Label:", cd.getRecordLabel());
        ProductDetailScreen.addDetailRow(detailPanel, "Genre:", cd.getGenre());
        ProductDetailScreen.addDetailRow(detailPanel, "Release Date:", cd.getReleaseDate());
        
        // Display track list
        List<String> tracks = cd.getTrackList();
        if (tracks != null && !tracks.isEmpty()) {
            detailPanel.add(Box.createVerticalStrut(SPACING_XSMALL));
            JLabel tracksLabel = new JLabel("Track List:");
            tracksLabel.setFont(FONT_BODY);
            tracksLabel.setForeground(TEXT_PRIMARY);
            tracksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailPanel.add(tracksLabel);
            
            for (String track : tracks) {
                JLabel trackItem = new JLabel("  • " + track);
                trackItem.setFont(FONT_SMALL);
                trackItem.setForeground(TEXT_SECONDARY);
                trackItem.setAlignmentX(Component.LEFT_ALIGNMENT);
                detailPanel.add(trackItem);
            }
        }
    }
}
