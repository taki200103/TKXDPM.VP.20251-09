package com.hust.soict.aims.boundaries.manager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.hust.soict.aims.controls.Database;
import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.utils.RoundedButton;
import com.hust.soict.aims.utils.RoundedPanel;
import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductFormDialog extends JDialog {
    private Product product;
    private boolean productSaved = false;

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

    public ProductFormDialog(Frame owner, Product product) {
        super(owner, product == null ? "Add Product" : "Edit Product", true);
        this.product = product;

        setSize(600, 700);
        setLocationRelativeTo(owner);
        setResizable(false);

        setupUI();
        bindEvents();

        if (product != null) {
            loadProductData();
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));

        JPanel mainPanel = new RoundedPanel(10, false);
        mainPanel.setBackground(BACKGROUND_WHITE);
        mainPanel.setBorder(PADDING_MEDIUM);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = new JLabel(product == null ? "Add New Product" : "Edit Product");
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Common fields
        mainPanel.add(createLabeledField("Type:", createTypeComboBox()));
        mainPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        mainPanel.add(createLabeledField("Title:", titleField = new JTextField()));
        mainPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        mainPanel.add(createLabeledField("Original Value (VND):", originalValueField = new JTextField()));
        mainPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        mainPanel.add(createLabeledField("Current Price (VND):", currentPriceField = new JTextField()));
        mainPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        mainPanel.add(createLabeledField("Weight (kg):", weightField = new JTextField()));
        mainPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        mainPanel.add(createLabeledField("Dimension:", dimensionField = new JTextField()));
        mainPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        mainPanel.add(createLabeledField("Barcode:", barcodeField = new JTextField()));
        mainPanel.add(Box.createVerticalStrut(SPACING_SMALL));

        // Description
        JPanel descPanel = new JPanel(new BorderLayout(SPACING_SMALL, 0));
        descPanel.setOpaque(false);
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(FONT_BODY);
        descLabel.setPreferredSize(new Dimension(150, 20));
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setFont(FONT_BODY);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        descPanel.add(descLabel, BorderLayout.WEST);
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        mainPanel.add(descPanel);
        mainPanel.add(Box.createVerticalStrut(SPACING_SMALL));

        // Type-specific fields panel
        typeSpecificPanel = new JPanel();
        typeSpecificPanel.setLayout(new BoxLayout(typeSpecificPanel, BoxLayout.Y_AXIS));
        typeSpecificPanel.setOpaque(false);
        typeSpecificPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeSpecificFields = new ArrayList<>();
        mainPanel.add(typeSpecificPanel);

        // Update type-specific fields when type changes
        typeComboBox.addActionListener(e -> updateTypeSpecificFields());
        updateTypeSpecificFields();

        mainPanel.add(Box.createVerticalGlue());

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_SMALL, 0));
        buttonPanel.setOpaque(false);

        RoundedButton saveButton = new RoundedButton("Save", 8);
        saveButton.setFont(FONT_BUTTON);
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(TEXT_ON_PRIMARY);
        saveButton.setCursor(CURSOR_HAND);
        saveButton.setPreferredSize(new Dimension(100, 40));
        saveButton.addActionListener(e -> saveProduct());

        RoundedButton cancelButton = new RoundedButton("Cancel", 8);
        cancelButton.setFont(FONT_BUTTON);
        cancelButton.setBackground(BACKGROUND_GRAY);
        cancelButton.setForeground(TEXT_PRIMARY);
        cancelButton.setCursor(CURSOR_HAND);
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(e -> setVisible(false));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JComboBox<String> createTypeComboBox() {
        String[] types = { "book", "cd", "dvd", "newspaper" };
        typeComboBox = new JComboBox<>(types);
        typeComboBox.setFont(FONT_BODY);
        typeComboBox.setPreferredSize(new Dimension(200, 35));
        return typeComboBox;
    }

    private JPanel createLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(FONT_BODY);
        labelComp.setPreferredSize(new Dimension(150, 35));

        if (field instanceof JTextField) {
            ((JTextField) field).setFont(FONT_BODY);
            ((JTextField) field).setPreferredSize(new Dimension(400, 35));
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        }

        panel.add(labelComp);
        panel.add(field);

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
                typeSpecificFields.add(createLabeledField("Pages:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Language:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Genre:", new JTextField()));
                break;
            case "cd":
                typeSpecificFields.add(createLabeledField("Album:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Artist:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Record Label:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Genre:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Release Date:", new JTextField()));
                break;
            case "dvd":
                typeSpecificFields.add(createLabeledField("Disc Type:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Director:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Runtime:", new JTextField()));
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
                typeSpecificFields.add(createLabeledField("Frequency:", new JTextField()));
                typeSpecificFields.add(createLabeledField("ISSN:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Language:", new JTextField()));
                typeSpecificFields.add(createLabeledField("Sections:", new JTextField()));
                break;
        }

        for (JComponent field : typeSpecificFields) {
            typeSpecificPanel.add(field);
            typeSpecificPanel.add(Box.createVerticalStrut(SPACING_XSMALL));
        }

        typeSpecificPanel.revalidate();
        typeSpecificPanel.repaint();
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
            JComponent field = panel.getComponentCount() > 1 ? (JComponent) panel.getComponent(1) : null;
            if (field instanceof JTextField) {
                ((JTextField) field).setText(value != null ? value : "");
            }
        }
    }

    private String getTypeField(int index) {
        if (index < typeSpecificFields.size()) {
            JPanel panel = (JPanel) typeSpecificFields.get(index);
            JComponent field = panel.getComponentCount() > 1 ? (JComponent) panel.getComponent(1) : null;
            if (field instanceof JTextField) {
                return ((JTextField) field).getText().trim();
            }
        }
        return "";
    }

    private void bindEvents() {
        // Enter key in fields moves to next field or saves
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
                    productSaved = true;
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add product", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing product
                newProduct.setId(product.getId());
                if (Database.updateProduct(newProduct)) {
                    JOptionPane.showMessageDialog(this, "Product updated successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    productSaved = true;
                    setVisible(false);
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
                b.setGenre(getTypeField(6));
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

    public boolean isProductSaved() {
        return productSaved;
    }
}
