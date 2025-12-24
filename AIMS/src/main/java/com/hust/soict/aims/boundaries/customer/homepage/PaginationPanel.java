package com.hust.soict.aims.boundaries.customer.homepage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.hust.soict.aims.utils.UIConstant.*;

public class PaginationPanel extends JPanel {
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;
    
    private int currentPage = 0;
    private int totalPages = 0;
    
    // Listeners
    private final List<PaginationListener> listeners = new ArrayList<>();
    
    /**
     * Listener interface for pagination events
     */
    public interface PaginationListener {
        void onPageChanged(int newPage);
    }
    
    /**
     * Constructor
     */
    public PaginationPanel() {
        setupUI();
        bindEvents();
    }
    
    /**
     * Setup the UI components
     */
    private void setupUI() {
        setLayout(new FlowLayout(FlowLayout.CENTER, SPACING_MEDIUM, SPACING_MEDIUM));
        setBackground(BACKGROUND_LIGHT);
        
        // Previous button
        prevButton = new JButton("Prev");
        prevButton.setFont(FONT_BUTTON);
        prevButton.setBackground(BACKGROUND_GRAY);
        prevButton.setFocusPainted(false);
        prevButton.setCursor(CURSOR_HAND);
        prevButton.setEnabled(false);
        
        // Page label
        pageLabel = new JLabel("Page 1 / 1");
        pageLabel.setFont(FONT_BODY);
        
        // Next button
        nextButton = new JButton("Next");
        nextButton.setFont(FONT_BUTTON);
        nextButton.setBackground(BACKGROUND_GRAY);
        nextButton.setFocusPainted(false);
        nextButton.setCursor(CURSOR_HAND);
        nextButton.setEnabled(false);
        
        // Add to panel
        add(prevButton);
        add(pageLabel);
        add(nextButton);
    }
    
    /**
     * Bind button events
     */
    private void bindEvents() {
        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                setCurrentPage(currentPage - 1, totalPages);
                notifyPageChanged();
            }
        });
        
        nextButton.addActionListener(e -> {
            if (currentPage < totalPages - 1) {
                setCurrentPage(currentPage + 1, totalPages);
                notifyPageChanged();
            }
        });
    }
    
    /**
     * Set current page and total pages
     * @param page Current page (0-based index)
     * @param total Total number of pages
     */
    public void setCurrentPage(int page, int total) {
        this.currentPage = page;
        this.totalPages = total;
        updateDisplay();
    }
    
    /**
     * Update display based on current state
     */
    private void updateDisplay() {
        // Update label
        pageLabel.setText(String.format("Page %d / %d", currentPage + 1, totalPages));
        
        // Update button states
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);
    }
    
    /**
     * Get current page (0-based)
     */
    public int getCurrentPage() {
        return currentPage;
    }
    
    /**
     * Get total pages
     */
    public int getTotalPages() {
        return totalPages;
    }
    
    /**
     * Add pagination listener
     */
    public void addPaginationListener(PaginationListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove pagination listener
     */
    public void removePaginationListener(PaginationListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners that page has changed
     */
    private void notifyPageChanged() {
        for (PaginationListener listener : listeners) {
            listener.onPageChanged(currentPage);
        }
    }
    
    /**
     * Reset to first page
     */
    public void reset() {
        setCurrentPage(0, totalPages);
    }
}
