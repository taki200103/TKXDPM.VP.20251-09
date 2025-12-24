package com.hust.soict.aims.boundaries.manager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.controls.Database;
import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.components.RoundedButton;
import com.hust.soict.aims.components.RoundedPanel;
import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductFormScreen extends BaseScreenHandler {
    private Product product;
    private BaseScreenHandler parentScreen;
    private ManagerMainScreen managerMainScreen;

    // Common fields
    private JTextField titleField;
    private JComboBox<String> typeComboBox;
    private JTextField originalValueField;
    private JTextField currentPriceField;
    private JTextField weightField;
    private JTextField dimensionField;
    private JTextArea descriptionArea;
    private JTextField barcodeField;

    // Type-specific fields
    private JPanel typeSpecificPanel;
    private List<JComponent> typeSpecificFields;

    public ProductFormScreen(BaseScreenHandler parent, Product product) {
        super(product == null ? "Add Product" : "Edit Product", parent, false);
        this.product = product;
        this.parentScreen = parent;
        // Store reference to ManagerMainScreen if parent is ManagerMainScreen
        if (parent instanceof ManagerMainScreen) {
            this.managerMainScreen = (ManagerMainScreen) parent;
        } else if (parent instanceof ProductManagementScreen) {
            ProductManagementScreen pms = (ProductManagementScreen) parent;
            BaseScreenHandler pmsParent = pms.getParentScreen();
            if (pmsParent instanceof ManagerMainScreen) {
                this.managerMainScreen = (ManagerMainScreen) pmsParent;
            }
        }
        initializeScreen();
    }

    @Override
    protected void initComponents() {
        // Initialize all fields
        titleField = new JTextField();
        typeComboBox = createTypeComboBox();
        originalValueField = new JTextField();
        currentPriceField = new JTextField();
        weightField = new JTextField();
        dimensionField = new JTextField();
        descriptionArea = new JTextArea(4, 40);
        barcodeField = new JTextField();
        
        typeSpecificPanel = new JPanel();
        typeSpecificPanel.setLayout(new BoxLayout(typeSpecificPanel, BoxLayout.Y_AXIS));
        typeSpecificPanel.setOpaque(false);
        typeSpecificFields = new ArrayList<>();
        
        // Update type-specific fields when type changes
        typeComboBox.addActionListener(e -> updateTypeSpecificFields());
        
        if (product != null) {
            loadProductData();
        } else {
            updateTypeSpecificFields();
        }
    }

    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        setBackground(BACKGROUND_LIGHT);
        
        // Wrapper panel with padding (exactly like ProductManagementScreen)
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(BACKGROUND_LIGHT);
        wrapperPanel.setBorder(PADDING_MEDIUM);
        
        // Main panel with form content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_WHITE);

        // Title
        JLabel titleLabel = new JLabel(product == null ? "Add New Product" : "Edit Product");
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(SPACING_LARGE));

        // Section 1: Basic Information
        JPanel basicInfoPanel = createSectionPanel("Basic Information");
        basicInfoPanel.add(createLabeledField("Product Type:", typeComboBox));
        basicInfoPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        basicInfoPanel.add(createLabeledField("Title:", titleField));
        basicInfoPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        basicInfoPanel.add(createLabeledField("Barcode:", barcodeField));
        mainPanel.add(basicInfoPanel);
        mainPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Section 2: Pricing & Physical Properties
        JPanel pricingPanel = createSectionPanel("Pricing & Physical Properties");
        pricingPanel.add(createLabeledField("Original Value (VND):", originalValueField));
        pricingPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        pricingPanel.add(createLabeledField("Current Price (VND):", currentPriceField));
        pricingPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        pricingPanel.add(createLabeledField("Weight (kg):", weightField));
        pricingPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        pricingPanel.add(createLabeledField("Dimension (WxHxL cm):", dimensionField));
        mainPanel.add(pricingPanel);
        mainPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Section 3: Description
        JPanel descSectionPanel = createSectionPanel("Description");
        JPanel descPanel = new JPanel(new BorderLayout(SPACING_SMALL, SPACING_SMALL));
        descPanel.setOpaque(false);
        descriptionArea.setFont(FONT_BODY);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        descPanel.add(descScrollPane, BorderLayout.CENTER);
        descSectionPanel.add(descPanel);
        mainPanel.add(descSectionPanel);
        mainPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Section 4: Type-Specific Information
        JPanel typeSectionPanel = createSectionPanel("Type-Specific Information");
        typeSectionPanel.add(typeSpecificPanel);
        mainPanel.add(typeSectionPanel);

        // Buttons panel at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_MEDIUM, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(PADDING_MEDIUM);

        RoundedButton saveButton = new RoundedButton("Save", 8);
        saveButton.setFont(FONT_BUTTON);
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(TEXT_ON_PRIMARY);
        saveButton.setCursor(CURSOR_HAND);
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.addActionListener(e -> saveProduct());

        RoundedButton cancelButton = new RoundedButton("Cancel", 8);
        cancelButton.setFont(FONT_BUTTON);
        cancelButton.setBackground(BACKGROUND_GRAY);
        cancelButton.setForeground(TEXT_PRIMARY);
        cancelButton.setCursor(CURSOR_HAND);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> {
            if (managerMainScreen != null) {
                // If embedded in ManagerMainScreen, go back to ProductManagement
                managerMainScreen.showProductManagement();
            } else {
                // Otherwise navigate back
                navigateBack();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Scroll pane for main content (similar to ProductManagementScreen)
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(PADDING_SMALL);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BACKGROUND_WHITE);
        
        // Add scroll pane to wrapper panel (exactly like ProductManagementScreen)
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add wrapper panel to content pane
        add(wrapperPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void bindEvents() {
        // Events are bound in initComponents and setupLayout
    }

    /**
     * Create a section panel with titled border
     */
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                title,
                0, 0,
                FONT_BUTTON,
                PRIMARY_COLOR
            ),
            BorderFactory.createEmptyBorder(SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JComboBox<String> createTypeComboBox() {
        String[] types = { "book", "cd", "dvd", "newspaper" };
        JComboBox<String> combo = new JComboBox<>(types);
        combo.setFont(FONT_BODY);
        combo.setPreferredSize(new Dimension(200, 35));
        return combo;
    }

    private JPanel createLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(SPACING_SMALL, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(FONT_BODY);
        labelComp.setForeground(TEXT_PRIMARY);
        labelComp.setPreferredSize(new Dimension(200, 35));
        labelComp.setMinimumSize(new Dimension(200, 35));

        if (field instanceof JTextField) {
            ((JTextField) field).setFont(FONT_BODY);
            ((JTextField) field).setPreferredSize(new Dimension(500, 35));
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setFont(FONT_BODY);
            ((JComboBox<?>) field).setPreferredSize(new Dimension(500, 35));
        }

        panel.add(labelComp, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void updateTypeSpecificFields() {
        typeSpecificPanel.removeAll();
        typeSpecificFields.clear();

        String type = (String) typeComboBox.getSelectedItem();

        switch (type) {
            case "book":
                typeSpecificFields.add(createLabeledField("Author:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Cover Type:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Publisher:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Publication Date:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Number of Pages:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Language:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Book Category:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Genre:", new JTextField()));
                break;
            case "cd":
                typeSpecificFields.add(createLabeledField("Album (Music Type):", new JTextField()));
                typeSpecificFields.add(createLabeledField("Artist:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Record Label:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Genre:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Release Date:", new JTextField()));
                break;
            case "dvd":
                typeSpecificFields.add(createLabeledField("Disc Type:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Director:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Runtime (minutes):", new JTextField()));
                typeSpecificFields.add(createLabeledField("Studio:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Language:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Subtitles:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Release Date:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Genre:", new JTextField()));
                break;
            case "newspaper":
                typeSpecificFields.add(createLabeledField("Editor in Chief:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Publisher:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Publication Date:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Issue Number:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Publication Frequency:", new JTextField()));
                typeSpecificFields.add(createLabeledField("ISSN:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Language:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Sections:", new JTextField()));
                break;
        }

        for (JComponent field : typeSpecificFields) {
            typeSpecificPanel.add(field);
            typeSpecificPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        }

        typeSpecificPanel.revalidate();
        typeSpecificPanel.repaint();
        
        // Repaint parent to update scroll
        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    private void loadProductData() {
        if (product == null)
            return;

        typeComboBox.setSelectedItem(product.getType());
        titleField.setText(product.getTitle());
        originalValueField.setText(String.valueOf((long) product.getOriginalValue()));
        currentPriceField.setText(String.valueOf((long) product.getCurrentPrice()));
        weightField.setText(String.valueOf(product.getWeight()));
        dimensionField.setText(product.getDimension());
        descriptionArea.setText(product.getDescription());
        barcodeField.setText(product.getBarcode() != null ? product.getBarcode() : "");

        updateTypeSpecificFields();

        // Load type-specific data
        String type = product.getType();
        int fieldIndex = 0;

        if (type.equals("book") && product instanceof Book) {
            Book b = (Book) product;
            setTypeField(fieldIndex++, b.getAuthor());
            setTypeField(fieldIndex++, b.getCoverType());
            setTypeField(fieldIndex++, b.getPublisher());
            setTypeField(fieldIndex++, b.getPublicationDate());
            setTypeField(fieldIndex++, b.getNumberOfPages() != null ? String.valueOf(b.getNumberOfPages()) : "");
            setTypeField(fieldIndex++, b.getLanguage());
            setTypeField(fieldIndex++, b.getBookCategory());
            setTypeField(fieldIndex++, b.getGenre());
        } else if (type.equals("cd") && product instanceof CD) {
            CD c = (CD) product;
            setTypeField(fieldIndex++, c.getAlbum());
            setTypeField(fieldIndex++, c.getArtist());
            setTypeField(fieldIndex++, c.getRecordLabel());
            setTypeField(fieldIndex++, c.getGenre());
            setTypeField(fieldIndex++, c.getReleaseDate());
        } else if (type.equals("dvd") && product instanceof DVD) {
            DVD d = (DVD) product;
            setTypeField(fieldIndex++, d.getDiscType());
            setTypeField(fieldIndex++, d.getDirector());
            setTypeField(fieldIndex++, d.getRuntime() != null ? String.valueOf(d.getRuntime()) : "");
            setTypeField(fieldIndex++, d.getStudio());
            setTypeField(fieldIndex++, d.getLanguage());
            setTypeField(fieldIndex++, d.getSubtitles());
            setTypeField(fieldIndex++, d.getReleaseDate());
            setTypeField(fieldIndex++, d.getGenre());
        } else if (type.equals("newspaper") && product instanceof Newspaper) {
            Newspaper n = (Newspaper) product;
            setTypeField(fieldIndex++, n.getEditorInChief());
            setTypeField(fieldIndex++, n.getPublisher());
            setTypeField(fieldIndex++, n.getPublicationDate());
            setTypeField(fieldIndex++, n.getIssueNumber());
            setTypeField(fieldIndex++, n.getPublicationFrequency());
            setTypeField(fieldIndex++, n.getIssn());
            setTypeField(fieldIndex++, n.getLanguage());
            setTypeField(fieldIndex++, n.getSections());
        }
    }

    private void setTypeField(int index, String value) {
        if (index < typeSpecificFields.size()) {
            JPanel panel = (JPanel) typeSpecificFields.get(index);
            JComponent field = (JComponent) panel.getComponent(1);
            if (field instanceof JTextField) {
                ((JTextField) field).setText(value != null ? value : "");
            }
        }
    }

    private String getTypeField(int index) {
        if (index < typeSpecificFields.size()) {
            JPanel panel = (JPanel) typeSpecificFields.get(index);
            JComponent field = (JComponent) panel.getComponent(1);
            if (field instanceof JTextField) {
                return ((JTextField) field).getText().trim();
            }
        }
        return "";
    }

    private void saveProduct() {
        // Validate common fields
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double originalValue = Double.parseDouble(originalValueField.getText().trim());
            double currentPrice = Double.parseDouble(currentPriceField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            String dimension = dimensionField.getText().trim();
            String description = descriptionArea.getText().trim();
            String barcode = barcodeField.getText().trim();
            String type = (String) typeComboBox.getSelectedItem();

            Product newProduct = createProductFromFields(type, originalValue, currentPrice, weight, dimension,
                    description, barcode);

            if (product == null) {
                // Add new product
                long newId = Database.addProduct(newProduct);
                if (newId > 0) {
                    JOptionPane.showMessageDialog(this, "Product added successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Go back to ProductManagement and refresh
                    if (managerMainScreen != null) {
                        managerMainScreen.showProductManagement();
                        if (managerMainScreen.productManagementScreen != null) {
                            managerMainScreen.productManagementScreen.refresh();
                        }
                    } else if (parentScreen != null) {
                        parentScreen.refresh();
                        navigateBack();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add product", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing product
                newProduct.setId(product.getId());
                if (Database.updateProduct(newProduct)) {
                    JOptionPane.showMessageDialog(this, "Product updated successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Go back to ProductManagement and refresh
                    if (managerMainScreen != null) {
                        managerMainScreen.showProductManagement();
                        if (managerMainScreen.productManagementScreen != null) {
                            managerMainScreen.productManagementScreen.refresh();
                        }
                    } else if (parentScreen != null) {
                        parentScreen.refresh();
                        navigateBack();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update product", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in price or weight fields", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Product createProductFromFields(String type, double originalValue, double currentPrice, double weight,
            String dimension, String description, String barcode) {
        String title = titleField.getText().trim();
        long id = product != null ? product.getId() : 0;

        Product p = null;

        switch (type) {
            case "book": {
                Book b = new Book(id, title, originalValue, currentPrice, weight, dimension, description,
                        getTypeField(0), getTypeField(1), getTypeField(2), getTypeField(3));
                String pages = getTypeField(4);
                if (!pages.isEmpty()) {
                    try {
                        b.setNumberOfPages(Integer.parseInt(pages));
                    } catch (NumberFormatException ignored) {
                    }
                }
                b.setLanguage(getTypeField(5));
                b.setBookCategory(getTypeField(6));
                b.setGenre(getTypeField(7));
                b.setBarcode(barcode.isEmpty() ? null : barcode);
                p = b;
                break;
            }
            case "cd": {
                CD c = new CD(id, title, originalValue, currentPrice, weight, dimension, description,
                        getTypeField(0), getTypeField(1), getTypeField(2));
                c.setGenre(getTypeField(3));
                c.setReleaseDate(getTypeField(4));
                c.setBarcode(barcode.isEmpty() ? null : barcode);
                p = c;
                break;
            }
            case "dvd": {
                DVD d = new DVD(id, title, originalValue, currentPrice, weight, dimension, description,
                        getTypeField(0), getTypeField(1));
                String runtimeStr = getTypeField(2);
                if (!runtimeStr.isEmpty()) {
                    try {
                        // Try to parse as integer (remove "min" or other text)
                        String numStr = runtimeStr.replaceAll("[^0-9]", "");
                        if (!numStr.isEmpty()) {
                            d.setRuntime(Integer.parseInt(numStr));
                        }
                    } catch (NumberFormatException e) {
                        // If parsing fails, use setRuntime(String) which will try to parse
                        d.setRuntime(runtimeStr);
                    }
                }
                d.setStudio(getTypeField(3));
                d.setLanguage(getTypeField(4));
                d.setSubtitles(getTypeField(5));
                d.setReleaseDate(getTypeField(6));
                d.setGenre(getTypeField(7));
                d.setBarcode(barcode.isEmpty() ? null : barcode);
                p = d;
                break;
            }
            case "newspaper": {
                Newspaper n = new Newspaper(id, title, originalValue, currentPrice, weight, dimension, description,
                        getTypeField(0), getTypeField(1), getTypeField(2));
                n.setIssueNumber(getTypeField(3));
                n.setPublicationFrequency(getTypeField(4));
                n.setIssn(getTypeField(5));
                n.setLanguage(getTypeField(6));
                n.setSections(getTypeField(7));
                n.setBarcode(barcode.isEmpty() ? null : barcode);
                p = n;
                break;
            }
            default: {
                p = new Product(id, title, originalValue, currentPrice, weight, dimension, description,
                        barcode.isEmpty() ? null : barcode, null);
                break;
            }
        }

        return p;
    }

    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        // Don't maximize if embedded in ManagerMainScreen
        if (managerMainScreen == null) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }
}
