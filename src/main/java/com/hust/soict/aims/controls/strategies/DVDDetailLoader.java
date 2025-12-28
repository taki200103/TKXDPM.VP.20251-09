package com.hust.soict.aims.controls.strategies;

import com.hust.soict.aims.entities.DVD;
import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.boundaries.ProductDetailScreen;
import javax.swing.JPanel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Strategy Pattern - Concrete Strategy for DVD products
 * 
 * This class implements the ProductDetailLoader interface specifically for DVD products.
 * It knows how to:
 * 1. Load additional DVD-specific data from the DVD table (JOIN with Media)
 * 2. Display all DVD-related information in the UI
 */
public class DVDDetailLoader implements ProductDetailLoader {
    
    @Override
    public Product loadDetails(Connection conn, Product product) {
        if (!(product instanceof DVD)) {
            return product;
        }
        
        DVD dvd = (DVD) product;
        long mediaId = dvd.getId();
        
        // Strategy Pattern: Load DVD-specific details by joining DVD table with Media
        String sql = "SELECT disc_type, director, runtime, studio, language, subtitle, " +
                     "release_date, genre FROM DVD WHERE media_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dvd.setDiscType(rs.getString("disc_type"));
                    dvd.setDirector(rs.getString("director"));
                    dvd.setRuntime(rs.getObject("runtime") != null ? rs.getInt("runtime") : null);
                    dvd.setStudio(rs.getString("studio"));
                    dvd.setLanguage(rs.getString("language"));
                    dvd.setSubtitles(rs.getString("subtitle"));
                    dvd.setReleaseDate(rs.getString("release_date"));
                    dvd.setGenre(rs.getString("genre"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading DVD details: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dvd;
    }
    
    @Override
    public void displayDetails(JPanel detailPanel, Product product) {
        if (!(product instanceof DVD)) {
            return;
        }
        
        DVD dvd = (DVD) product;
        
        // Strategy Pattern: Display all DVD-specific information
        ProductDetailScreen.addDetailRow(detailPanel, "Disc Type:", dvd.getDiscType());
        ProductDetailScreen.addDetailRow(detailPanel, "Director:", dvd.getDirector());
        
        String runtimeStr = dvd.getRuntime() != null ? 
                           String.valueOf(dvd.getRuntime()) + " min" : "N/A";
        ProductDetailScreen.addDetailRow(detailPanel, "Runtime:", runtimeStr);
        
        ProductDetailScreen.addDetailRow(detailPanel, "Studio:", dvd.getStudio());
        ProductDetailScreen.addDetailRow(detailPanel, "Language:", dvd.getLanguage());
        ProductDetailScreen.addDetailRow(detailPanel, "Subtitles:", dvd.getSubtitles());
        ProductDetailScreen.addDetailRow(detailPanel, "Release Date:", dvd.getReleaseDate());
        ProductDetailScreen.addDetailRow(detailPanel, "Genre:", dvd.getGenre());
    }
}
