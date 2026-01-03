package com.hust.soict.aims.controls.strategies;

import com.hust.soict.aims.entities.DVD;
import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.boundaries.ProductDetailScreen;
import com.hust.soict.aims.dao.DVDDAO;
import com.hust.soict.aims.dao.impl.DVDDAOImpl;
import javax.swing.JPanel;
import java.sql.Connection;

/**
 * Strategy Pattern - Concrete Strategy for DVD products
 * 
 * This class implements the ProductDetailLoader interface specifically for DVD products.
 * It knows how to:
 * 1. Load additional DVD-specific data from the DVD table (JOIN with Media)
 * 2. Display all DVD-related information in the UI
 */
public class DVDDetailLoader implements ProductDetailLoader {
    
    private DVDDAO dvdDAO;
    
    public DVDDetailLoader() {
        this.dvdDAO = new DVDDAOImpl();
    }
    
    @Override
    public Product loadDetails(Connection conn, Product product) {
        if (!(product instanceof DVD)) {
            return product;
        }
        
        DVD dvd = (DVD) product;
        long mediaId = dvd.getId();
        
        // Strategy Pattern: Load DVD-specific details using DAO
        dvdDAO.loadDVDDetails(conn, dvd, mediaId);
        
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
