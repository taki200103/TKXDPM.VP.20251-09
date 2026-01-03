package com.hust.soict.aims.controls.strategies;

import com.hust.soict.aims.entities.CD;
import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.entities.Track;
import com.hust.soict.aims.boundaries.ProductDetailScreen;
import com.hust.soict.aims.controls.Database;
import com.hust.soict.aims.dao.CDDAO;
import com.hust.soict.aims.dao.TrackDAO;
import com.hust.soict.aims.dao.impl.CDDAOImpl;
import com.hust.soict.aims.dao.impl.TrackDAOImpl;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.Connection;
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
    
    private CDDAO cdDAO;
    private TrackDAO trackDAO;
    
    public CDDetailLoader() {
        this.cdDAO = new CDDAOImpl();
        this.trackDAO = new TrackDAOImpl();
    }
    
    @Override
    public Product loadDetails(Connection conn, Product product) {
        if (!(product instanceof CD)) {
            return product;
        }
        
        CD cd = (CD) product;
        long mediaId = cd.getId();
        
        // Strategy Pattern: Load CD-specific details using DAO
        cdDAO.loadCDDetails(conn, cd, mediaId);
        
        // Strategy Pattern: Load Track information using DAO
        List<Track> tracks = trackDAO.loadTracks(mediaId);
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
        
        // Display track list with full information
        detailPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        JLabel tracksLabel = new JLabel("Track List:");
        tracksLabel.setFont(FONT_BODY);
        tracksLabel.setForeground(PRIMARY_COLOR);
        tracksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(tracksLabel);
        detailPanel.add(Box.createVerticalStrut(SPACING_XSMALL));
        
        // Load tracks from database to display full information using DAO
        List<Track> tracks = trackDAO.loadTracks(cd.getId());
        if (tracks != null && !tracks.isEmpty()) {
            // Create a panel for track list with better formatting
            JPanel trackListPanel = new JPanel();
            trackListPanel.setLayout(new BoxLayout(trackListPanel, BoxLayout.Y_AXIS));
            trackListPanel.setOpaque(false);
            trackListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            trackListPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                    BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_MEDIUM, SPACING_SMALL, SPACING_MEDIUM)));
            
            for (Track track : tracks) {
                JPanel trackRow = new JPanel();
                trackRow.setLayout(new BoxLayout(trackRow, BoxLayout.X_AXIS));
                trackRow.setOpaque(false);
                trackRow.setAlignmentX(Component.LEFT_ALIGNMENT);
                trackRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                
                // Track number label
                Integer trackNumber = track.getTrackNumber();
                String trackNumText = trackNumber != null ? String.valueOf(trackNumber) : "?";
                JLabel trackNumLabel = new JLabel("Track #" + trackNumText + ":");
                trackNumLabel.setFont(new java.awt.Font(FONT_FAMILY, java.awt.Font.BOLD, FONT_SIZE_BODY));
                trackNumLabel.setForeground(PRIMARY_COLOR);
                trackNumLabel.setPreferredSize(new Dimension(90, 25));
                trackRow.add(trackNumLabel);
                trackRow.add(Box.createHorizontalStrut(SPACING_XSMALL));
                
                // Title label
                JLabel titleLabelLabel = new JLabel("title:");
                titleLabelLabel.setFont(FONT_BODY);
                titleLabelLabel.setForeground(TEXT_SECONDARY);
                titleLabelLabel.setPreferredSize(new Dimension(50, 25));
                trackRow.add(titleLabelLabel);
                trackRow.add(Box.createHorizontalStrut(SPACING_XSMALL));
                
                // Title value
                String title = track.getTitle() != null ? track.getTitle() : "";
                JLabel titleValueLabel = new JLabel(title);
                titleValueLabel.setFont(FONT_BODY);
                titleValueLabel.setForeground(TEXT_PRIMARY);
                titleValueLabel.setPreferredSize(new Dimension(250, 25));
                trackRow.add(titleValueLabel);
                trackRow.add(Box.createHorizontalStrut(SPACING_SMALL));
                
                // Length label
                JLabel lengthLabelLabel = new JLabel("length:");
                lengthLabelLabel.setFont(FONT_BODY);
                lengthLabelLabel.setForeground(TEXT_SECONDARY);
                lengthLabelLabel.setPreferredSize(new Dimension(60, 25));
                trackRow.add(lengthLabelLabel);
                trackRow.add(Box.createHorizontalStrut(SPACING_XSMALL));
                
                // Length value
                if (track.getLength() != null) {
                    int minutes = track.getLength() / 60;
                    int seconds = track.getLength() % 60;
                    String lengthText = String.format("%d:%02d", minutes, seconds);
                    JLabel lengthValueLabel = new JLabel(lengthText);
                    lengthValueLabel.setFont(FONT_BODY);
                    lengthValueLabel.setForeground(TEXT_PRIMARY);
                    lengthValueLabel.setPreferredSize(new Dimension(60, 25));
                    trackRow.add(lengthValueLabel);
                } else {
                    JLabel lengthValueLabel = new JLabel("N/A");
                    lengthValueLabel.setFont(FONT_BODY);
                    lengthValueLabel.setForeground(TEXT_SECONDARY);
                    lengthValueLabel.setPreferredSize(new Dimension(60, 25));
                    trackRow.add(lengthValueLabel);
                }
                
                trackRow.add(Box.createHorizontalGlue());
                trackListPanel.add(trackRow);
                trackListPanel.add(Box.createVerticalStrut(SPACING_XSMALL));
            }
            
            detailPanel.add(trackListPanel);
        } else {
            // Fallback to trackList if database tracks not available
            List<String> trackList = cd.getTrackList();
            if (trackList != null && !trackList.isEmpty()) {
                for (String track : trackList) {
                    JLabel trackItem = new JLabel("  • " + track);
                    trackItem.setFont(FONT_SMALL);
                    trackItem.setForeground(TEXT_SECONDARY);
                    trackItem.setAlignmentX(Component.LEFT_ALIGNMENT);
                    detailPanel.add(trackItem);
                }
            } else {
                JLabel noTracksLabel = new JLabel("  No tracks available");
                noTracksLabel.setFont(FONT_SMALL);
                noTracksLabel.setForeground(TEXT_SECONDARY);
                noTracksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                detailPanel.add(noTracksLabel);
            }
        }
    }
}
