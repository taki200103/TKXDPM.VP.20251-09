package com.hust.soict.aims.boundaries.manager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.controls.Database;
import com.hust.soict.aims.controls.ProductController;
import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.utils.RoundedButton;
import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductManagementScreen extends BaseScreenHandler {
    private ProductController productController;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public ProductManagementScreen(BaseScreenHandler parent) {
        super("Product Management", parent, false);
        this.productController = new ProductController();
        initializeScreen();
    }

    @Override
    protected void initComponents() {
        // Initialize table model
        String[] columnNames = { "ID", "Type", "Title", "Price (VND)", "Weight (kg)", "Stock" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        productTable = new JTable(tableModel);
        productTable.setFont(FONT_BODY);
        productTable.setRowHeight(30);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setFont(FONT_BUTTON);
        productTable.getTableHeader().setBackground(PRIMARY_COLOR);
        productTable.getTableHeader().setForeground(TEXT_ON_PRIMARY);

        // Buttons
        addButton = new RoundedButton("Add Product", 8);
        addButton.setFont(FONT_BUTTON);
        addButton.setBackground(SUCCESS_COLOR);
        addButton.setForeground(TEXT_ON_PRIMARY);
        addButton.setCursor(CURSOR_HAND);
        addButton.setPreferredSize(new Dimension(130, 40));

        editButton = new RoundedButton("Edit", 8);
        editButton.setFont(FONT_BUTTON);
        editButton.setBackground(PRIMARY_COLOR);
        editButton.setForeground(TEXT_ON_PRIMARY);
        editButton.setCursor(CURSOR_HAND);
        editButton.setPreferredSize(new Dimension(100, 40));

        deleteButton = new RoundedButton("Delete", 8);
        deleteButton.setFont(FONT_BUTTON);
        deleteButton.setBackground(DANGER_COLOR);
        deleteButton.setForeground(TEXT_ON_PRIMARY);
        deleteButton.setCursor(CURSOR_HAND);
        deleteButton.setPreferredSize(new Dimension(100, 40));

        refreshButton = new RoundedButton("Refresh", 8);
        refreshButton.setFont(FONT_BUTTON);
        refreshButton.setBackground(BACKGROUND_GRAY);
        refreshButton.setForeground(TEXT_PRIMARY);
        refreshButton.setCursor(CURSOR_HAND);
        refreshButton.setPreferredSize(new Dimension(100, 40));
    }

    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        setBackground(BACKGROUND_LIGHT);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(PADDING_MEDIUM);
        headerPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));

        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_SMALL, SPACING_SMALL));
        toolbarPanel.setBackground(BACKGROUND_WHITE);
        toolbarPanel.setBorder(PADDING_SMALL);

        toolbarPanel.add(addButton);
        toolbarPanel.add(editButton);
        toolbarPanel.add(deleteButton);
        toolbarPanel.add(Box.createHorizontalStrut(SPACING_MEDIUM));
        toolbarPanel.add(refreshButton);

        add(toolbarPanel, BorderLayout.NORTH);

        // Table with scroll
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(PADDING_SMALL);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    protected void bindEvents() {
        addButton.addActionListener(e -> showAddProductDialog());
        editButton.addActionListener(e -> showEditProductDialog());
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        refreshButton.addActionListener(e -> refresh());
    }

    @Override
    public void refresh() {
        tableModel.setRowCount(0);

        // Load all products
        List<Product> products = productController.getPage(0);
        int total = productController.countProducts();

        // Load all products (we need to get all, not just one page)
        for (int i = 0; i < total; i += productController.getPageSize()) {
            List<Product> pageProducts = productController.getPage(i / productController.getPageSize());
            for (Product product : pageProducts) {
                addProductToTable(product);
            }
        }

        productTable.revalidate();
        productTable.repaint();
    }

    private void addProductToTable(Product product) {
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(java.util.Locale.getDefault());
        symbols.setGroupingSeparator(',');
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###", symbols);

        Object[] row = {
                product.getId(),
                product.getType(),
                product.getTitle(),
                df.format((long) product.getCurrentPrice()),
                String.format("%.2f", product.getWeight()),
                Database.getStock(product.getId())
        };
        tableModel.addRow(row);
    }

    private void showAddProductDialog() {
        ProductFormDialog dialog = new ProductFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isProductSaved()) {
            refresh();
        }
    }

    private void showEditProductDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        long productId = (Long) tableModel.getValueAt(selectedRow, 0);
        Product product = Database.getProductById(productId);

        if (product != null) {
            ProductFormDialog dialog = new ProductFormDialog(this, product);
            dialog.setVisible(true);
            if (dialog.isProductSaved()) {
                refresh();
            }
        }
    }

    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        long productId = (Long) tableModel.getValueAt(selectedRow, 0);
        String productTitle = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete:\n" + productTitle + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (Database.deleteProduct(productId)) {
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

    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        refresh();
    }
}
