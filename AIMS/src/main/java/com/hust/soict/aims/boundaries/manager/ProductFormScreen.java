package com.hust.soict.aims.boundaries.manager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.controls.Database;
import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.entities.enums.*;
import com.hust.soict.aims.components.RoundedButton;
import com.hust.soict.aims.components.RoundedPanel;
import com.hust.soict.aims.utils.ImageUtils;
import static com.hust.soict.aims.utils.UIConstant.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

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
    private JTextField quantityField;
    private JComboBox<ProductCondition> conditionComboBox;
    private JComboBox<ProductStatus> statusComboBox;

    // Type-specific fields
    private JPanel typeSpecificPanel;
    private List<JComponent> typeSpecificFields;
    
    // Image upload fields
    private File selectedImageFile;
    private JLabel imagePreviewLabel;
    private RoundedButton uploadImageButton;

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
        quantityField = new JTextField();
        
        // Initialize condition and status dropdowns
        conditionComboBox = new JComboBox<>(ProductCondition.values());
        conditionComboBox.setFont(FONT_BODY);
        conditionComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProductCondition) {
                    setText(((ProductCondition) value).getValue());
                }
                return this;
            }
        });
        
        statusComboBox = new JComboBox<>(ProductStatus.values());
        statusComboBox.setFont(FONT_BODY);
        statusComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProductStatus) {
                    setText(((ProductStatus) value).getValue());
                }
                return this;
            }
        });
        
        typeSpecificPanel = new JPanel();
        typeSpecificPanel.setLayout(new BoxLayout(typeSpecificPanel, BoxLayout.Y_AXIS));
        typeSpecificPanel.setOpaque(false);
        typeSpecificFields = new ArrayList<>();
        
        // Initialize image upload components
        selectedImageFile = null;
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setVerticalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(200, 200));
        imagePreviewLabel.setMinimumSize(new Dimension(200, 200));
        imagePreviewLabel.setMaximumSize(new Dimension(200, 200));
        imagePreviewLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        imagePreviewLabel.setBackground(BACKGROUND_WHITE);
        imagePreviewLabel.setOpaque(true);
        imagePreviewLabel.setText("<html><center>No Image<br>Selected</center></html>");
        imagePreviewLabel.setForeground(TEXT_SECONDARY);
        
        uploadImageButton = new RoundedButton("Upload Image", 8);
        uploadImageButton.setFont(FONT_BUTTON);
        uploadImageButton.setBackground(PRIMARY_COLOR);
        uploadImageButton.setForeground(TEXT_ON_PRIMARY);
        uploadImageButton.setCursor(CURSOR_HAND);
        uploadImageButton.setPreferredSize(new Dimension(150, 35));
        uploadImageButton.addActionListener(e -> selectImageFile());
        
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
        basicInfoPanel.add(createLabeledField("Product Type:", typeComboBox, true));
        basicInfoPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        basicInfoPanel.add(createLabeledField("Title:", titleField, true));
        basicInfoPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        basicInfoPanel.add(createLabeledField("Barcode:", barcodeField));
        mainPanel.add(basicInfoPanel);
        mainPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Section 2: Pricing & Physical Properties
        JPanel pricingPanel = createSectionPanel("Pricing & Physical Properties");
        pricingPanel.add(createLabeledField("Original Value (VND):", originalValueField, true));
        pricingPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        pricingPanel.add(createLabeledField("Current Price (VND):", currentPriceField, true));
        pricingPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        pricingPanel.add(createLabeledField("Weight (kg):", weightField, true));
        pricingPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        pricingPanel.add(createLabeledField("Quantity (Stock):", quantityField, true));
        pricingPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        pricingPanel.add(createLabeledField("Dimension (WxHxL cm):", dimensionField));
        pricingPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        pricingPanel.add(createLabeledField("Condition:", conditionComboBox));
        pricingPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        pricingPanel.add(createLabeledField("Status:", statusComboBox));
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

        // Section 3.5: Product Image
        JPanel imageSectionPanel = createSectionPanel("Product Image");
        JPanel imagePanel = new JPanel(new BorderLayout(SPACING_MEDIUM, SPACING_SMALL));
        imagePanel.setOpaque(false);
        
        JPanel imagePreviewPanel = new JPanel(new BorderLayout());
        imagePreviewPanel.setOpaque(false);
        imagePreviewPanel.add(imagePreviewLabel, BorderLayout.CENTER);
        
        JPanel uploadButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        uploadButtonPanel.setOpaque(false);
        uploadButtonPanel.add(uploadImageButton);
        
        imagePanel.add(imagePreviewPanel, BorderLayout.CENTER);
        imagePanel.add(uploadButtonPanel, BorderLayout.SOUTH);
        imageSectionPanel.add(imagePanel);
        mainPanel.add(imageSectionPanel);
        mainPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Section 4: Type-Specific Information
        JPanel typeSectionPanel = createSectionPanel("Type-Specific Information");
        typeSectionPanel.add(typeSpecificPanel);
        mainPanel.add(typeSectionPanel);

        // Buttons panel at bottom
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_MEDIUM, 0));
        bottomButtonPanel.setOpaque(false);
        bottomButtonPanel.setBorder(PADDING_MEDIUM);

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

        bottomButtonPanel.add(saveButton);
        bottomButtonPanel.add(cancelButton);

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
        add(bottomButtonPanel, BorderLayout.SOUTH);
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
        return createLabeledField(label, field, false);
    }
    
    private JPanel createLabeledField(String label, JComponent field, boolean required) {
        JPanel panel = new JPanel(new BorderLayout(SPACING_SMALL, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        String labelText = label;
        if (required) {
            labelText = label + " *";
        }
        JLabel labelComp = new JLabel(labelText);
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
                // Cover Type as dropdown
                JComboBox<BookCoverType> coverTypeCombo = new JComboBox<>(BookCoverType.values());
                coverTypeCombo.setFont(FONT_BODY);
                coverTypeCombo.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                            boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof BookCoverType) {
                            setText(((BookCoverType) value).getValue());
                        }
                        return this;
                    }
                });
                typeSpecificFields.add(createLabeledField("Cover Type:", coverTypeCombo));
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
                // Disc Type as dropdown
                JComboBox<DVDDiscType> discTypeCombo = new JComboBox<>(DVDDiscType.values());
                discTypeCombo.setFont(FONT_BODY);
                discTypeCombo.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                            boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof DVDDiscType) {
                            setText(((DVDDiscType) value).getValue());
                        }
                        return this;
                    }
                });
                typeSpecificFields.add(createLabeledField("Disc Type:", discTypeCombo));
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
        quantityField.setText(String.valueOf(product.getQuantity()));
        dimensionField.setText(product.getDimension());
        descriptionArea.setText(product.getDescription());
        barcodeField.setText(product.getBarcode() != null ? product.getBarcode() : "");
        
        // Load condition and status
        if (product.getCondition() != null) {
            conditionComboBox.setSelectedItem(ProductCondition.fromString(product.getCondition()));
        }
        if (product.getStatus() != null) {
            statusComboBox.setSelectedItem(ProductStatus.fromString(product.getStatus()));
        }

        // Load product image if exists
        loadProductImage();

        updateTypeSpecificFields();

        // Load type-specific data
        String type = product.getType();
        int fieldIndex = 0;

        if (type.equals("book") && product instanceof Book) {
            Book b = (Book) product;
            setTypeField(fieldIndex++, b.getAuthor());
            // Set cover type dropdown
            if (b.getCoverType() != null) {
                setTypeFieldCombo(fieldIndex, BookCoverType.fromString(b.getCoverType()));
            }
            fieldIndex++;
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
            // Set disc type dropdown
            if (d.getDiscType() != null) {
                setTypeFieldCombo(fieldIndex, DVDDiscType.fromString(d.getDiscType()));
            }
            fieldIndex++;
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
    
    private void setTypeFieldCombo(int index, Object value) {
        if (index < typeSpecificFields.size()) {
            JPanel panel = (JPanel) typeSpecificFields.get(index);
            JComponent field = (JComponent) panel.getComponent(1);
            if (field instanceof JComboBox) {
                ((JComboBox<?>) field).setSelectedItem(value);
            }
        }
    }

    private String getTypeField(int index) {
        if (index < typeSpecificFields.size()) {
            JPanel panel = (JPanel) typeSpecificFields.get(index);
            JComponent field = (JComponent) panel.getComponent(1);
            if (field instanceof JTextField) {
                return ((JTextField) field).getText().trim();
            } else if (field instanceof JComboBox) {
                Object selected = ((JComboBox<?>) field).getSelectedItem();
                if (selected != null) {
                    if (selected instanceof BookCoverType) {
                        return ((BookCoverType) selected).getValue();
                    } else if (selected instanceof DVDDiscType) {
                        return ((DVDDiscType) selected).getValue();
                    }
                    return selected.toString();
                }
            }
        }
        return "";
    }

    private void saveProduct() {
        // Validate required fields
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required (*)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            titleField.requestFocus();
            return;
        }
        
        if (originalValueField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Original Value is required (*)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            originalValueField.requestFocus();
            return;
        }
        
        if (currentPriceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Current Price is required (*)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            currentPriceField.requestFocus();
            return;
        }
        
        if (weightField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Weight is required (*)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            weightField.requestFocus();
            return;
        }
        
        if (quantityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quantity is required (*)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            quantityField.requestFocus();
            return;
        }

        try {
            double originalValue = Double.parseDouble(originalValueField.getText().trim());
            double currentPrice = Double.parseDouble(currentPriceField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than or equal to 0", "Validation Error", JOptionPane.ERROR_MESSAGE);
                quantityField.requestFocus();
                return;
            }
            
            if (originalValue < 0) {
                JOptionPane.showMessageDialog(this, "Original Value must be greater than or equal to 0", "Validation Error", JOptionPane.ERROR_MESSAGE);
                originalValueField.requestFocus();
                return;
            }
            
            if (currentPrice < 0) {
                JOptionPane.showMessageDialog(this, "Current Price must be greater than or equal to 0", "Validation Error", JOptionPane.ERROR_MESSAGE);
                currentPriceField.requestFocus();
                return;
            }
            
            if (weight <= 0) {
                JOptionPane.showMessageDialog(this, "Weight must be greater than 0", "Validation Error", JOptionPane.ERROR_MESSAGE);
                weightField.requestFocus();
                return;
            }
            
            String dimension = dimensionField.getText().trim();
            String description = descriptionArea.getText().trim();
            String barcode = barcodeField.getText().trim();
            String type = (String) typeComboBox.getSelectedItem();

            // Get condition and status
            ProductCondition condition = (ProductCondition) conditionComboBox.getSelectedItem();
            ProductStatus status = (ProductStatus) statusComboBox.getSelectedItem();
            
            Product newProduct = createProductFromFields(type, originalValue, currentPrice, weight, dimension,
                    description, barcode, condition, status, quantity);

            if (product == null) {
                // Add new product
                long newId = Database.addProduct(newProduct);
                if (newId > 0) {
                    // Save image if one was selected
                    if (selectedImageFile != null) {
                        ImageUtils.saveProductImage(selectedImageFile, newId);
                    }
                    
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
                    // Save image if one was selected
                    if (selectedImageFile != null) {
                        ImageUtils.saveProductImage(selectedImageFile, product.getId());
                    }
                    
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
            String dimension, String description, String barcode, ProductCondition condition, ProductStatus status, int quantity) {
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
                b.setQuantity(quantity);
                if (condition != null) b.setCondition(condition.getValue());
                if (status != null) b.setStatus(status.getValue());
                p = b;
                break;
            }
            case "cd": {
                CD c = new CD(id, title, originalValue, currentPrice, weight, dimension, description,
                        getTypeField(0), getTypeField(1), getTypeField(2));
                c.setGenre(getTypeField(3));
                c.setReleaseDate(getTypeField(4));
                c.setBarcode(barcode.isEmpty() ? null : barcode);
                c.setQuantity(quantity);
                if (condition != null) c.setCondition(condition.getValue());
                if (status != null) c.setStatus(status.getValue());
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
                d.setQuantity(quantity);
                if (condition != null) d.setCondition(condition.getValue());
                if (status != null) d.setStatus(status.getValue());
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
                n.setQuantity(quantity);
                if (condition != null) n.setCondition(condition.getValue());
                if (status != null) n.setStatus(status.getValue());
                p = n;
                break;
            }
            default: {
                p = new Product(id, title, originalValue, currentPrice, weight, dimension, description,
                        barcode.isEmpty() ? null : barcode, null);
                p.setQuantity(quantity);
                if (condition != null) p.setCondition(condition.getValue());
                if (status != null) p.setStatus(status.getValue());
                break;
            }
        }

        return p;
    }

    /**
     * Handle image file selection
     */
    private void selectImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Product Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Image Files (*.jpg, *.jpeg, *.png, *.gif)", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            displayImagePreview(selectedImageFile);
        }
    }
    
    /**
     * Display image preview in the preview label
     */
    private void displayImagePreview(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("<html><center>No Image<br>Selected</center></html>");
            return;
        }
        
        try {
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            Image image = icon.getImage();
            
            // Scale image to fit preview label (200x200)
            int previewSize = 180; // Leave some padding
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            
            double scale = Math.min((double) previewSize / width, (double) previewSize / height);
            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);
            
            Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
            imagePreviewLabel.setText(null);
        } catch (Exception e) {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("<html><center>Error<br>Loading Image</center></html>");
            e.printStackTrace();
        }
    }
    
    /**
     * Load existing product image if available
     */
    private void loadProductImage() {
        if (product == null || product.getId() <= 0) {
            return;
        }
        
        String imagePath = ImageUtils.getProductImagePath(product.getId());
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                displayImagePreview(imageFile);
                // Don't set selectedImageFile here - only set it when user explicitly selects a new image
            }
        }
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
