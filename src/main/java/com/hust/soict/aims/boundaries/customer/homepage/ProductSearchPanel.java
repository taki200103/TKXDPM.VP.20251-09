package com.hust.soict.aims.boundaries.customer.homepage;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.hust.soict.aims.components.RoundedPanel;
import com.hust.soict.aims.components.RoundedButton;
import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductSearchPanel extends RoundedPanel {
    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> priceRangeComboBox;
    private JButton searchButton;
    private JButton clearButton;

    private final List<SearchListener> listeners = new ArrayList<>();

    // Price ranges in VND
    private static final String[] PRICE_RANGES = {
            "All",
            "< 100,000 VND",
            "100,000 - 200,000 VND",
            "200,000 - 300,000 VND",
            "300,000 - 500,000 VND",
            "> 500,000 VND"
    };

    private static final String[] CATEGORIES = {
            "All",
            "Book",
            "CD",
            "DVD",
            "Newspaper"
    };

    /**
     * Listener interface for search events
     */
    public interface SearchListener {
        void onSearchChanged(String searchTerm, String category, Double minPrice, Double maxPrice);
    }

    /**
     * Constructor
     */
    public ProductSearchPanel() {
        super(10, false); // Rounded corners without shadow
        setBackground(BACKGROUND_WHITE);
        setBorder(BorderFactory.createEmptyBorder(SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM));
        setupUI();
        bindEvents();
    }

    /**
     * Setup the UI components
     */
    private void setupUI() {
        setLayout(new BorderLayout(SPACING_SMALL, SPACING_SMALL));

        // Top row: Search field and Clear button
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_SMALL, SPACING_SMALL));
        topRow.setOpaque(false);

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(FONT_BODY);
        searchLabel.setForeground(TEXT_PRIMARY);

        searchField = new JTextField();
        searchField.setFont(FONT_BODY);
        searchField.setPreferredSize(new Dimension(400, 38));
        searchField.setToolTipText("Enter product name to search");
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        // Search button (orange) with rounded corners
        searchButton = new RoundedButton("Search", 8);
        searchButton.setFont(FONT_BUTTON);
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(TEXT_ON_PRIMARY);
        searchButton.setCursor(CURSOR_HAND);
        searchButton.setPreferredSize(new Dimension(110, 38));

        topRow.add(searchLabel);
        topRow.add(searchField);
        topRow.add(searchButton);

        // Bottom row: Category and Price filters
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_SMALL, SPACING_SMALL));
        bottomRow.setOpaque(false);

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(FONT_BODY);
        categoryLabel.setForeground(TEXT_PRIMARY);

        categoryComboBox = new JComboBox<>(CATEGORIES);
        categoryComboBox.setFont(FONT_BODY);
        categoryComboBox.setPreferredSize(new Dimension(160, 38));
        categoryComboBox.setToolTipText("Select product category");

        JLabel priceLabel = new JLabel("Price Range:");
        priceLabel.setFont(FONT_BODY);
        priceLabel.setForeground(TEXT_PRIMARY);

        priceRangeComboBox = new JComboBox<>(PRICE_RANGES);
        priceRangeComboBox.setFont(FONT_BODY);
        priceRangeComboBox.setPreferredSize(new Dimension(220, 38));
        priceRangeComboBox.setToolTipText("Select price range");

        // Clear button (moved to bottom row) with rounded corners
        clearButton = new RoundedButton("Clear", 8);
        clearButton.setFont(FONT_BUTTON);
        clearButton.setBackground(BACKGROUND_GRAY);
        clearButton.setForeground(TEXT_PRIMARY);
        clearButton.setCursor(CURSOR_HAND);
        clearButton.setPreferredSize(new Dimension(90, 38));

        bottomRow.add(categoryLabel);
        bottomRow.add(categoryComboBox);
        bottomRow.add(Box.createHorizontalStrut(SPACING_MEDIUM));
        bottomRow.add(priceLabel);
        bottomRow.add(priceRangeComboBox);
        bottomRow.add(Box.createHorizontalStrut(SPACING_MEDIUM));
        bottomRow.add(clearButton);

        // Add rows to main panel
        add(topRow, BorderLayout.NORTH);
        add(bottomRow, BorderLayout.CENTER);
    }

    /**
     * Bind events
     */
    private void bindEvents() {
        // Enter key in search field
        searchField.addActionListener(e -> performSearch());

        // Search button
        searchButton.addActionListener(e -> performSearch());

        // Clear button
        clearButton.addActionListener(e -> {
            searchField.setText("");
            categoryComboBox.setSelectedIndex(0);
            priceRangeComboBox.setSelectedIndex(0);
            performSearch();
        });

        // Category filter change
        categoryComboBox.addActionListener(e -> performSearch());

        // Price range filter change
        priceRangeComboBox.addActionListener(e -> performSearch());

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
        String category = (String) categoryComboBox.getSelectedItem();
        if (category != null && category.equals("All")) {
            category = null;
        }

        // Parse price range
        String priceRange = (String) priceRangeComboBox.getSelectedItem();
        Double minPrice = null;
        Double maxPrice = null;

        if (priceRange != null && !priceRange.equals("All")) {
            if (priceRange.equals("< 100,000 VND")) {
                maxPrice = 100000.0;
            } else if (priceRange.equals("100,000 - 200,000 VND")) {
                minPrice = 100000.0;
                maxPrice = 200000.0;
            } else if (priceRange.equals("200,000 - 300,000 VND")) {
                minPrice = 200000.0;
                maxPrice = 300000.0;
            } else if (priceRange.equals("300,000 - 500,000 VND")) {
                minPrice = 300000.0;
                maxPrice = 500000.0;
            } else if (priceRange.equals("> 500,000 VND")) {
                minPrice = 500000.0;
            }
        }

        notifySearchChanged(searchTerm, category, minPrice, maxPrice);
    }

    /**
     * Get current search term
     */
    public String getSearchTerm() {
        return searchField.getText().trim();
    }

    /**
     * Get current category filter
     */
    public String getCategory() {
        String category = (String) categoryComboBox.getSelectedItem();
        return (category != null && !category.equals("All")) ? category : null;
    }

    /**
     * Get current price range
     */
    public Double[] getPriceRange() {
        String priceRange = (String) priceRangeComboBox.getSelectedItem();
        Double minPrice = null;
        Double maxPrice = null;

        if (priceRange != null && !priceRange.equals("All")) {
            if (priceRange.equals("< 100,000 VND")) {
                maxPrice = 100000.0;
            } else if (priceRange.equals("100,000 - 200,000 VND")) {
                minPrice = 100000.0;
                maxPrice = 200000.0;
            } else if (priceRange.equals("200,000 - 300,000 VND")) {
                minPrice = 200000.0;
                maxPrice = 300000.0;
            } else if (priceRange.equals("300,000 - 500,000 VND")) {
                minPrice = 300000.0;
                maxPrice = 500000.0;
            } else if (priceRange.equals("> 500,000 VND")) {
                minPrice = 500000.0;
            }
        }

        return new Double[] { minPrice, maxPrice };
    }

    /**
     * Set search term programmatically
     */
    public void setSearchTerm(String term) {
        searchField.setText(term);
    }

    /**
     * Clear search field and filters
     */
    public void clear() {
        searchField.setText("");
        categoryComboBox.setSelectedIndex(0);
        priceRangeComboBox.setSelectedIndex(0);
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
    private void notifySearchChanged(String searchTerm, String category, Double minPrice, Double maxPrice) {
        for (SearchListener listener : listeners) {
            listener.onSearchChanged(searchTerm, category, minPrice, maxPrice);
        }
    }

    /**
     * Request focus on search field
     */
    public void focusSearchField() {
        searchField.requestFocusInWindow();
    }
}
