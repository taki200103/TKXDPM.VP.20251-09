package com.hust.soict.aims.boundaries.manager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.controls.Database;
import com.hust.soict.aims.controls.ProductController;
import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.components.RoundedButton;
import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductManagementScreen extends BaseScreenHandler {
    private ProductController productController;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> categoryFilter;
    private String currentSearchTerm = "";
    private String currentCategory = null;
    private ManagerMainScreen managerMainScreen;

    public ProductManagementScreen(BaseScreenHandler parent) {
        super("Product Management", parent, false);
        this.productController = new ProductController();
        // Store reference to ManagerMainScreen if parent is ManagerMainScreen
        if (parent instanceof ManagerMainScreen) {
            this.managerMainScreen = (ManagerMainScreen) parent;
        }
        initializeScreen();
    }

    @Override
    protected void initComponents() {
        // Initialize table model with Actions column (ID column hidden)
        String[] columnNames = { "Type", "Title", "Price (VND)", "Weight (kg)", "Stock", "Status", "Actions" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only Actions column is editable (for buttons)
                return column == 6;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 6) {
                    return JPanel.class; // Actions column contains JPanel with buttons
                }
                return Object.class;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setFont(FONT_BODY);
        productTable.setRowHeight(50); // Increased height for buttons
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setFont(FONT_BUTTON);
        productTable.getTableHeader().setBackground(PRIMARY_COLOR);
        productTable.getTableHeader().setForeground(TEXT_ON_PRIMARY);
        
        // Set column widths (ID column is hidden)
        productTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // Type
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);   // Title
        productTable.getColumnModel().getColumn(2).setPreferredWidth(120);   // Price
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);   // Weight
        productTable.getColumnModel().getColumn(4).setPreferredWidth(80);    // Stock
        productTable.getColumnModel().getColumn(5).setPreferredWidth(100);   // Status
        productTable.getColumnModel().getColumn(6).setPreferredWidth(150);   // Actions
        
        // Set renderer and editor for Actions column
        productTable.getColumn("Actions").setCellRenderer(new ButtonPanelRenderer());
        productTable.getColumn("Actions").setCellEditor(new ButtonPanelEditor());

        // Add button
        addButton = new RoundedButton("Add Product", 8);
        addButton.setFont(FONT_BUTTON);
        addButton.setBackground(SUCCESS_COLOR);
        addButton.setForeground(TEXT_ON_PRIMARY);
        addButton.setCursor(CURSOR_HAND);
        addButton.setPreferredSize(new Dimension(130, 40));
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(FONT_BODY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Search button
        searchButton = new RoundedButton("Search", 8);
        searchButton.setFont(FONT_BUTTON);
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(TEXT_ON_PRIMARY);
        searchButton.setCursor(CURSOR_HAND);
        searchButton.setPreferredSize(new Dimension(100, 35));
        
        // Category filter dropdown
        String[] categories = {"All Categories", "Book", "CD", "DVD", "Newspaper"};
        categoryFilter = new JComboBox<>(categories);
        categoryFilter.setFont(FONT_BODY);
        categoryFilter.setPreferredSize(new Dimension(150, 35));
        categoryFilter.setSelectedIndex(0); // Default to "All Categories"
    }

    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        setBackground(BACKGROUND_LIGHT);
        // Create a wrapper panel with padding instead of setting border on content pane
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_LIGHT);
        mainPanel.setBorder(PADDING_MEDIUM);

        // Top panel: Search and Add button
        JPanel topPanel = new JPanel(new BorderLayout(SPACING_MEDIUM, SPACING_SMALL));
        topPanel.setBackground(BACKGROUND_WHITE);
        topPanel.setBorder(PADDING_SMALL);
        
        // Left: Search and Category Filter
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_SMALL, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(SPACING_MEDIUM));
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(categoryFilter);
        
        // Right: Add button
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        addPanel.setOpaque(false);
        addPanel.add(addButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(addPanel, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table with scroll
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(PADDING_SMALL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add main panel to content pane
        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    protected void bindEvents() {
        addButton.addActionListener(e -> showAddProductDialog());
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch()); // Search on Enter
        categoryFilter.addActionListener(e -> performSearch()); // Search when category changes
    }

    /**
     * Perform search
     */
    private void performSearch() {
        currentSearchTerm = searchField.getText().trim();
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            // Normalize category to lowercase for case-insensitive comparison
            currentCategory = selectedCategory.toLowerCase();
        } else {
            currentCategory = null;
        }
        refresh();
    }

    @Override
    public void refresh() {
        // Stop any cell editing before clearing table to prevent index issues
        if (productTable.isEditing()) {
            productTable.getCellEditor().stopCellEditing();
        }
        
        tableModel.setRowCount(0);

        List<Product> products;
        
        // Check if we have any filters (search term or category)
        boolean hasFilters = !currentSearchTerm.isEmpty() || currentCategory != null;
        
        if (!hasFilters) {
            // Load all products (including deactivated) for management
            int total = productController.countAllProductsForManagement();
            int pageSize = productController.getPageSize();
            for (int i = 0; i < total; i += pageSize) {
                List<Product> pageProducts = productController.getAllProductsForManagement(i, pageSize);
                for (Product product : pageProducts) {
                    addProductToTable(product);
                }
            }
        } else {
            // Use filtered search - load all pages (including deactivated)
            int pageIndex = 0;
            int pageSize = productController.getPageSize();
            do {
                products = productController.searchProductsWithFiltersForManagement(
                    currentSearchTerm.isEmpty() ? null : currentSearchTerm,
                    currentCategory,
                    null, // minPrice
                    null, // maxPrice
                    pageIndex * pageSize,
                    pageSize
                );
                for (Product product : products) {
                    addProductToTable(product);
                }
                pageIndex++;
            } while (!products.isEmpty() && products.size() >= pageSize);
        }

        productTable.revalidate();
        productTable.repaint();
    }

    private void addProductToTable(Product product) {
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(java.util.Locale.getDefault());
        symbols.setGroupingSeparator(',');
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###", symbols);

        // Create action buttons panel
        JPanel actionPanel = createActionButtonsPanel(product.getId());

        // Get status display text
        String statusText = product.getStatus() != null ? product.getStatus() : "active";
        // Capitalize first letter
        statusText = statusText.substring(0, 1).toUpperCase() + statusText.substring(1);

        // Store productId as hidden data - we'll use it for buttons
        // Note: ID is not displayed but we need it for actions
        Object[] row = {
                product.getType(),
                product.getTitle(),
                df.format((long) product.getCurrentPrice()),
                String.format("%.2f", product.getWeight()),
                productController.getStock(product.getId()),
                statusText,
                actionPanel
        };
        tableModel.addRow(row);
        
        // Store productId in the row for later retrieval
        // We'll use a custom approach: store it in the actionPanel's client property
        actionPanel.putClientProperty("productId", product.getId());
    }
    
    /**
     * Create action buttons panel for a product row
     */
    private JPanel createActionButtonsPanel(long productId) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);
        
        // Update button - increased width to show full text
        RoundedButton updateButton = new RoundedButton("Update", 6);
        updateButton.setFont(FONT_SMALL);
        updateButton.setBackground(PRIMARY_COLOR);
        updateButton.setForeground(TEXT_ON_PRIMARY);
        updateButton.setCursor(CURSOR_HAND);
        updateButton.setPreferredSize(new Dimension(85, 30));
        updateButton.setMinimumSize(new Dimension(85, 30));
        updateButton.setMaximumSize(new Dimension(85, 30));
        updateButton.addActionListener(e -> showEditProductDialog(productId));
        
        // Delete button - increased width to show full text
        RoundedButton deleteButton = new RoundedButton("Delete", 6);
        deleteButton.setFont(FONT_SMALL);
        deleteButton.setBackground(DANGER_COLOR);
        deleteButton.setForeground(TEXT_ON_PRIMARY);
        deleteButton.setCursor(CURSOR_HAND);
        deleteButton.setPreferredSize(new Dimension(85, 30));
        deleteButton.setMinimumSize(new Dimension(85, 30));
        deleteButton.setMaximumSize(new Dimension(85, 30));
        deleteButton.addActionListener(e -> deleteProduct(productId));
        
        panel.add(updateButton);
        panel.add(deleteButton);
        
        return panel;
    }
    
    /**
     * Button panel renderer for Actions column
     */
    private class ButtonPanelRenderer extends JPanel implements TableCellRenderer {
        public ButtonPanelRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            // Get productId from the Actions column value (JPanel with client property)
            if (value instanceof JPanel) {
                JPanel actionPanel = (JPanel) value;
                Object productIdObj = actionPanel.getClientProperty("productId");
                if (productIdObj instanceof Long || productIdObj instanceof Integer || productIdObj instanceof Number) {
                    long productId = ((Number) productIdObj).longValue();
                    // Create new buttons panel for this row
                    JPanel buttonsPanel = createActionButtonsPanel(productId);
                    // Add all components from the buttons panel
                    for (Component comp : buttonsPanel.getComponents()) {
                        add(comp);
                    }
                }
            }
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }
    
    /**
     * Button panel editor for Actions column
     */
    private class ButtonPanelEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private long currentProductId;
        
        public ButtonPanelEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            panel.removeAll();
            // Get productId from the Actions column value (JPanel with client property)
            // Use the row parameter to ensure we get the correct productId for this specific row
            if (value instanceof JPanel) {
                JPanel actionPanel = (JPanel) value;
                Object productIdObj = actionPanel.getClientProperty("productId");
                if (productIdObj instanceof Long || productIdObj instanceof Integer || productIdObj instanceof Number) {
                    currentProductId = ((Number) productIdObj).longValue();
                } else {
                    // Fallback: try to get from table model if client property is missing
                    // This shouldn't happen, but just in case
                    System.err.println("[ButtonPanelEditor] Warning: productId not found in actionPanel for row " + row);
                    return panel;
                }
            } else {
                // If value is not a JPanel, something is wrong
                System.err.println("[ButtonPanelEditor] Error: value is not JPanel for row " + row);
                return panel;
            }
            
            // Create new buttons panel for this row with the correct productId
            JPanel buttonsPanel = createActionButtonsPanel(currentProductId);
            // Add all components from the buttons panel
            for (Component comp : buttonsPanel.getComponents()) {
                panel.add(comp);
            }
            
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            // Return a new JPanel with productId to preserve the value in the table model
            JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            resultPanel.putClientProperty("productId", currentProductId);
            return resultPanel;
        }
    }

    private void showAddProductDialog() {
        if (managerMainScreen != null) {
            managerMainScreen.showProductForm(null);
        } else {
            // Fallback: navigate to new screen if not embedded
            ProductFormScreen screen = new ProductFormScreen(this, null);
            navigateTo(screen);
        }
    }

    private void showEditProductDialog(long productId) {
        Product product = productController.getProductById(productId);
        if (product != null) {
            if (managerMainScreen != null) {
                managerMainScreen.showProductForm(product);
            } else {
                // Fallback: navigate to new screen if not embedded
                ProductFormScreen screen = new ProductFormScreen(this, product);
                navigateTo(screen);
            }
        }
    }

    private void deleteProduct(long productId) {
        Product product = productController.getProductById(productId);
        if (product == null) {
            return;
        }
        
        String productTitle = product.getTitle();
        int stock = productController.getStock(productId);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete:\n" + productTitle + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (stock > 0) {
                // Stock > 0: only deactivate
                if (productController.deleteProduct(productId)) {
                    JOptionPane.showMessageDialog(this,
                            "Product cannot be deleted because stock > 0.\nStatus changed to 'Deactivated'.",
                            "Product Deactivated",
                            JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to deactivate product",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Stock = 0: delete
                if (productController.deleteProduct(productId)) {
                    JOptionPane.showMessageDialog(this,
                            "Product deleted successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete product",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        // Don't maximize if embedded in another screen
        if (getParent() == null || !(getParent() instanceof JPanel)) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        refresh();
    }
}
