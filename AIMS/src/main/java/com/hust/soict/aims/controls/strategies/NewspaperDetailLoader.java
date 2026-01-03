package com.hust.soict.aims.controls.strategies;

import com.hust.soict.aims.entities.Newspaper;
import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.boundaries.ProductDetailScreen;
import com.hust.soict.aims.dao.NewspaperDAO;
import com.hust.soict.aims.dao.impl.NewspaperDAOImpl;
import javax.swing.JPanel;
import java.sql.Connection;

/**
 * Strategy Pattern - Concrete Strategy for Newspaper products
 * 
 * This class implements the ProductDetailLoader interface specifically for Newspaper products.
 * It knows how to:
 * 1. Load additional Newspaper-specific data from the Newspaper table (JOIN with Media)
 * 2. Display all Newspaper-related information in the UI
 */
public class NewspaperDetailLoader implements ProductDetailLoader {
    
    private NewspaperDAO newspaperDAO;
    
    public NewspaperDetailLoader() {
        this.newspaperDAO = new NewspaperDAOImpl();
    }
    
    @Override
    public Product loadDetails(Connection conn, Product product) {
        if (!(product instanceof Newspaper)) {
            return product;
        }
        
        Newspaper newspaper = (Newspaper) product;
        long mediaId = newspaper.getId();
        
        // Strategy Pattern: Load Newspaper-specific details using DAO
        newspaperDAO.loadNewspaperDetails(conn, newspaper, mediaId);
        
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
