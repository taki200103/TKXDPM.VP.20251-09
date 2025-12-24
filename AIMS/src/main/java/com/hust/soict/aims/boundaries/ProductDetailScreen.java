package com.hust.soict.aims.boundaries;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.List;
import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.utils.ImageUtils;
import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductDetailScreen extends JDialog {
    public ProductDetailScreen(Frame owner, Product p) {
        super(owner, "Product Details", true);
        setLayout(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        setSize(700, 600);
        setLocationRelativeTo(owner);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        mainPanel.setBorder(PADDING_MEDIUM);
        mainPanel.setBackground(BACKGROUND_WHITE);
        
        // Top panel: Image and basic info
        JPanel topPanel = new JPanel(new BorderLayout(SPACING_MEDIUM, SPACING_MEDIUM));
        topPanel.setBackground(BACKGROUND_WHITE);
        
        // Left: Product image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(BACKGROUND_LIGHT);
        imagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            PADDING_MEDIUM
        ));
        imagePanel.setPreferredSize(new Dimension(250, 250));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Get image path (use ImageUtils to get path from product ID)
        String imagePath = p.getImagePath();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = ImageUtils.getProductImagePath(p.getId());
        } else {
            // Verify the path exists, if not try to get from ID
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                imagePath = ImageUtils.getProductImagePath(p.getId());
            }
        }
        
        // Try to load image if path exists
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage();
                Image scaledImg = img.getScaledInstance(230, 230, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                imageLabel.setText("<html><center>No<br>Image</center></html>");
                imageLabel.setFont(FONT_BODY);
                imageLabel.setForeground(TEXT_SECONDARY);
            }
        } else {
            // Placeholder for no image
            imageLabel.setText("<html><center>No<br>Image</center></html>");
            imageLabel.setFont(FONT_BODY);
            imageLabel.setForeground(TEXT_SECONDARY);
        }
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Right: Basic product info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BACKGROUND_WHITE);
        infoPanel.setBorder(PADDING_SMALL);
        
        // Title
        JLabel titleLabel = new JLabel(p.getTitle());
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        
        // Barcode
        JPanel barcodePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barcodePanel.setBackground(BACKGROUND_WHITE);
        barcodePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel barcodeLabel = new JLabel("Barcode:");
        barcodeLabel.setFont(FONT_BODY);
        barcodeLabel.setForeground(TEXT_SECONDARY);
        
        JLabel barcodeValue = new JLabel(p.getBarcode() != null ? p.getBarcode() : String.valueOf(p.getId()));
        barcodeValue.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_BODY));
        barcodeValue.setForeground(TEXT_PRIMARY);
        barcodeValue.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        barcodePanel.add(barcodeLabel);
        barcodePanel.add(Box.createHorizontalStrut(SPACING_SMALL));
        barcodePanel.add(barcodeValue);
        infoPanel.add(barcodePanel);
        infoPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));
        
        // Price
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(java.util.Locale.getDefault());
        symbols.setGroupingSeparator(',');
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###", symbols);
        JLabel priceLabel = new JLabel(String.format("Price: %s VND", df.format((long)p.getCurrentPrice())));
        priceLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_PRODUCT_NAME));
        priceLabel.setForeground(PRIMARY_COLOR);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(priceLabel);
        
        if (p.getOriginalValue() > p.getCurrentPrice()) {
            JLabel originalPriceLabel = new JLabel(String.format("Original: %s VND", df.format((long)p.getOriginalValue())));
            originalPriceLabel.setFont(FONT_SMALL);
            originalPriceLabel.setForeground(TEXT_SECONDARY);
            originalPriceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(originalPriceLabel);
        }
        
        infoPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));
        
        // Type
        JLabel typeLabel = new JLabel("Type: " + getTypeName(p));
        typeLabel.setFont(FONT_BODY);
        typeLabel.setForeground(TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(typeLabel);
        
        topPanel.add(imagePanel, BorderLayout.WEST);
        topPanel.add(infoPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center: Detailed information
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(BACKGROUND_WHITE);
        detailPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            "Product Details",
            0, 0,
            FONT_BODY,
            TEXT_PRIMARY
        ));
        
        // Common product info
        addDetailRow(detailPanel, "Weight:", String.format("%.2f kg", p.getWeight()));
        addDetailRow(detailPanel, "Dimension:", p.getDimension());
        addDetailRow(detailPanel, "Description:", p.getDescription());
        detailPanel.add(Box.createVerticalStrut(SPACING_SMALL));
        
        // Type-specific info
        if (p instanceof Book) {
            Book b = (Book) p;
            addDetailRow(detailPanel, "Author:", b.getAuthor());
            addDetailRow(detailPanel, "Cover:", b.getCoverType());
            addDetailRow(detailPanel, "Publisher:", b.getPublisher());
            addDetailRow(detailPanel, "Publication Date:", b.getPublicationDate());
            if (b.getNumberOfPages() != null) {
                addDetailRow(detailPanel, "Pages:", String.valueOf(b.getNumberOfPages()));
            }
            addDetailRow(detailPanel, "Language:", b.getLanguage());
            addDetailRow(detailPanel, "Genre:", b.getGenre());
        } else if (p instanceof Newspaper) {
            Newspaper n = (Newspaper) p;
            addDetailRow(detailPanel, "Editor in Chief:", n.getEditorInChief());
            addDetailRow(detailPanel, "Publisher:", n.getPublisher());
            addDetailRow(detailPanel, "Publication Date:", n.getPublicationDate());
            addDetailRow(detailPanel, "Issue Number:", n.getIssueNumber());
            addDetailRow(detailPanel, "Frequency:", n.getPublicationFrequency());
            addDetailRow(detailPanel, "ISSN:", n.getIssn());
            addDetailRow(detailPanel, "Language:", n.getLanguage());
            addDetailRow(detailPanel, "Sections:", n.getSections());
        } else if (p instanceof CD) {
            CD c = (CD) p;
            addDetailRow(detailPanel, "Album:", c.getAlbum());
            addDetailRow(detailPanel, "Artist:", c.getArtist());
            addDetailRow(detailPanel, "Record Label:", c.getRecordLabel());
            addDetailRow(detailPanel, "Genre:", c.getGenre());
            addDetailRow(detailPanel, "Release Date:", c.getReleaseDate());
            List<String> tracks = c.getTrackList();
            if (tracks != null && !tracks.isEmpty()) {
                detailPanel.add(Box.createVerticalStrut(SPACING_XSMALL));
                JLabel tracksLabel = new JLabel("Track List:");
                tracksLabel.setFont(FONT_BODY);
                tracksLabel.setForeground(TEXT_PRIMARY);
                tracksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                detailPanel.add(tracksLabel);
                for (String t : tracks) {
                    JLabel trackItem = new JLabel("  • " + t);
                    trackItem.setFont(FONT_SMALL);
                    trackItem.setForeground(TEXT_SECONDARY);
                    trackItem.setAlignmentX(Component.LEFT_ALIGNMENT);
                    detailPanel.add(trackItem);
                }
            }
        } else if (p instanceof DVD) {
            DVD d = (DVD) p;
            addDetailRow(detailPanel, "Disc Type:", d.getDiscType());
            addDetailRow(detailPanel, "Director:", d.getDirector());
            String runtimeStr = d.getRuntime() != null ? d.getRuntime() + " min" : "N/A";
            addDetailRow(detailPanel, "Runtime:", runtimeStr);
            addDetailRow(detailPanel, "Studio:", d.getStudio());
            addDetailRow(detailPanel, "Language:", d.getLanguage());
            addDetailRow(detailPanel, "Subtitles:", d.getSubtitles());
            addDetailRow(detailPanel, "Release Date:", d.getReleaseDate());
            addDetailRow(detailPanel, "Genre:", d.getGenre());
        }
        
        JScrollPane detailScrollPane = new JScrollPane(detailPanel);
        detailScrollPane.setBorder(null);
        detailScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(detailScrollPane, BorderLayout.CENTER);
        
        // Bottom: Close button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BACKGROUND_WHITE);
        com.hust.soict.aims.utils.RoundedButton close = new com.hust.soict.aims.utils.RoundedButton("Close", 8);
        close.setFont(FONT_BUTTON);
        close.setBackground(PRIMARY_COLOR);
        close.setForeground(TEXT_ON_PRIMARY);
        close.setCursor(CURSOR_HAND);
        close.setPreferredSize(BUTTON_SIZE_MEDIUM);
        close.addActionListener(e -> setVisible(false));
        bottomPanel.add(close);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        if (value == null || value.isEmpty()) return;
        
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(BACKGROUND_WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(FONT_BODY);
        labelComp.setForeground(TEXT_SECONDARY);
        labelComp.setPreferredSize(new Dimension(150, 20));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(FONT_BODY);
        valueComp.setForeground(TEXT_PRIMARY);
        
        row.add(labelComp);
        row.add(valueComp);
        panel.add(row);
        panel.add(Box.createVerticalStrut(SPACING_XSMALL));
    }
    
    private String getTypeName(Product p) {
        if (p instanceof Book) return "Book";
        if (p instanceof CD) return "CD";
        if (p instanceof DVD) return "DVD";
        if (p instanceof Newspaper) return "Newspaper";
        return "Product";
    }
}
