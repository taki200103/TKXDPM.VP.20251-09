package com.hust.soict.aims.controls.strategies;

import com.hust.soict.aims.entities.Newspaper;
import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.boundaries.ProductDetailScreen;
import javax.swing.JPanel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Strategy Pattern - Concrete Strategy for Newspaper products
 * 
 * This class implements the ProductDetailLoader interface specifically for Newspaper products.
 * It knows how to:
 * 1. Load additional Newspaper-specific data from the Newspaper table (JOIN with Media)
 * 2. Display all Newspaper-related information in the UI
 */
public class NewspaperDetailLoader implements ProductDetailLoader {
    
    @Override
    public Product loadDetails(Connection conn, Product product) {
        if (!(product instanceof Newspaper)) {
            return product;
        }
        
        Newspaper newspaper = (Newspaper) product;
        long mediaId = newspaper.getId();
        
        // Strategy Pattern: Load Newspaper-specific details by joining Newspaper table with Media
        String sql = "SELECT editor_in_chief, publisher, publish_date, issue_number, " +
                     "publication_frequency, issn, language, sections " +
                     "FROM Newspaper WHERE media_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    newspaper.setEditorInChief(rs.getString("editor_in_chief"));
                    newspaper.setPublisher(rs.getString("publisher"));
                    newspaper.setPublicationDate(rs.getString("publish_date"));
                    newspaper.setIssueNumber(rs.getString("issue_number"));
                    newspaper.setPublicationFrequency(rs.getString("publication_frequency"));
                    newspaper.setIssn(rs.getString("issn"));
                    newspaper.setLanguage(rs.getString("language"));
                    newspaper.setSections(rs.getString("sections"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading Newspaper details: " + e.getMessage());
            e.printStackTrace();
        }
        
        return newspaper;
    }
    
    @Override
    public void displayDetails(JPanel detailPanel, Product product) {
        if (!(product instanceof Newspaper)) {
            return;
        }
        
        Newspaper newspaper = (Newspaper) product;
        
        // Strategy Pattern: Display all Newspaper-specific information
        ProductDetailScreen.addDetailRow(detailPanel, "Editor in Chief:", newspaper.getEditorInChief());
        ProductDetailScreen.addDetailRow(detailPanel, "Publisher:", newspaper.getPublisher());
        ProductDetailScreen.addDetailRow(detailPanel, "Publication Date:", newspaper.getPublicationDate());
        ProductDetailScreen.addDetailRow(detailPanel, "Issue Number:", newspaper.getIssueNumber());
        ProductDetailScreen.addDetailRow(detailPanel, "Publication Frequency:", newspaper.getPublicationFrequency());
        ProductDetailScreen.addDetailRow(detailPanel, "ISSN:", newspaper.getIssn());
        ProductDetailScreen.addDetailRow(detailPanel, "Language:", newspaper.getLanguage());
        ProductDetailScreen.addDetailRow(detailPanel, "Sections:", newspaper.getSections());
    }
}
