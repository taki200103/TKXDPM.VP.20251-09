package com.hust.soict.aims.boundaries.customer.homepage;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductSearchPanel extends JPanel {
    private JTextField searchField;
    private JButton clearButton;
    
    private final List<SearchListener> listeners = new ArrayList<>();
    
    /**
     * Listener interface for search events
     */
    public interface SearchListener {
        void onSearchChanged(String searchTerm);
    }
    
    /**
     * Constructor
     */
    public ProductSearchPanel() {
        setupUI();
        bindEvents();
    }
    
    /**
     * Setup the UI components
     */
    private void setupUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT, SPACING_SMALL, SPACING_SMALL));
        setBackground(BACKGROUND_LIGHT);
        
        // Search label
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(FONT_BODY);
        
        // Search field
        searchField = new JTextField();
        searchField.setFont(FONT_BODY);
        searchField.setPreferredSize(new Dimension(815, 35));
        searchField.setToolTipText("Enter product name and press Enter to search");
        
        // Clear button
        clearButton = new JButton("Clear");
        clearButton.setFont(FONT_BUTTON);
        clearButton.setBackground(BACKGROUND_GRAY);
        clearButton.setFocusPainted(false);
        clearButton.setCursor(CURSOR_HAND);
        clearButton.setPreferredSize(new Dimension(80, 35));
        
        // Add components
        add(searchLabel);
        add(searchField);
        add(clearButton);
    }
    
    /**
     * Bind events
     */
    private void bindEvents() {
        // Enter key in search field
        searchField.addActionListener(e -> performSearch());
        
        // Clear button
        clearButton.addActionListener(e -> {
            searchField.setText("");
            performSearch();
        });
        
        // Real-time search (optional - search as user types)
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // Uncomment to enable real-time search
                // performSearch();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                // Uncomment to enable real-time search
                // performSearch();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not used for plain text fields
            }
        });
    }
    
    /**
     * Perform search and notify listeners
     */
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        notifySearchChanged(searchTerm);
    }
    
    /**
     * Get current search term
     */
    public String getSearchTerm() {
        return searchField.getText().trim();
    }
    
    /**
     * Set search term programmatically
     */
    public void setSearchTerm(String term) {
        searchField.setText(term);
    }
    
    /**
     * Clear search field
     */
    public void clear() {
        searchField.setText("");
    }
    
    /**
     * Add search listener
     */
    public void addSearchListener(SearchListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove search listener
     */
    public void removeSearchListener(SearchListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners that search term has changed
     */
    private void notifySearchChanged(String searchTerm) {
        for (SearchListener listener : listeners) {
            listener.onSearchChanged(searchTerm);
        }
    }
    
    /**
     * Request focus on search field
     */
    public void focusSearchField() {
        searchField.requestFocusInWindow();
    }
}

