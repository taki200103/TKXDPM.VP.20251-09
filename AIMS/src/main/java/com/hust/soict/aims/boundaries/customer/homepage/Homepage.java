package com.hust.soict.aims.boundaries.customer.homepage;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.hust.soict.aims.controls.ProductController;
import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.boundaries.ProductDetailScreen;
import com.hust.soict.aims.boundaries.LoginScreen;
import com.hust.soict.aims.boundaries.customer.cart.CartScreen;
import com.hust.soict.aims.controls.CartController;
import com.hust.soict.aims.entities.Product;
import static com.hust.soict.aims.utils.UIConstant.*;

public class Homepage extends BaseScreenHandler {
    private final ProductController productController;
    private final CartController cartController;

    private JPanel gridPanel;
    private PaginationPanel paginationPanel;
    private ProductSearchPanel searchPanel;
    private com.hust.soict.aims.utils.RoundedButton cartButton;
    private com.hust.soict.aims.utils.RoundedButton loginButton;

    // Current search filters
    private String currentSearchTerm = "";
    private String currentCategory = null;
    private Double currentMinPrice = null;
    private Double currentMaxPrice = null;

    public Homepage(ProductController productController, CartController cartController) {
        super("AIMS - Homepage", null, false);

        this.productController = productController;
        this.cartController = cartController;

        // Disable navigation for Homepage
        setNavigationEnabled(false);

        initializeScreen();
    }

    @Override
    protected void initComponents() {
        // Initialize grid panel for products with better spacing
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(PRODUCT_GRID_ROWS, PRODUCT_GRID_COLS,
                PRODUCT_GRID_HGAP + 5, PRODUCT_GRID_VGAP + 5));
        gridPanel.setBackground(BACKGROUND_LIGHT);
        gridPanel.setOpaque(true);

        // Initialize search panel
        searchPanel = new ProductSearchPanel();

        // Initialize pagination panel
        paginationPanel = new PaginationPanel();

        // Initialize cart button with rounded corners
        cartButton = new com.hust.soict.aims.utils.RoundedButton(getCartButtonText(), 8);
        cartButton.setFont(FONT_BUTTON);
        cartButton.setBackground(new Color(255, 255, 255, 30)); // Semi-transparent white
        cartButton.setForeground(TEXT_ON_PRIMARY);
        cartButton.setCursor(CURSOR_HAND);
        cartButton.setPreferredSize(new Dimension(120, 40));

        // Initialize login button
        loginButton = new com.hust.soict.aims.utils.RoundedButton("Login", 8);
        loginButton.setFont(FONT_BUTTON);
        loginButton.setBackground(new Color(255, 255, 255, 30)); // Semi-transparent white
        loginButton.setForeground(TEXT_ON_PRIMARY);
        loginButton.setCursor(CURSOR_HAND);
        loginButton.setPreferredSize(new Dimension(100, 40));
    }

    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_LIGHT);

        // Top Panel (Header + Search)
        JPanel topPanel = new JPanel(new BorderLayout(0, SPACING_MEDIUM));
        topPanel.setBackground(BACKGROUND_LIGHT);
        topPanel.setBorder(
                BorderFactory.createEmptyBorder(SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM));

        // Header Panel with rounded bottom corners
        JPanel headerPanel = new JPanel(new BorderLayout(SPACING_MEDIUM, 0));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(SPACING_MEDIUM, SPACING_LARGE, SPACING_MEDIUM, SPACING_LARGE));
        headerPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT + 10));

        // Left: Logo and Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_SMALL, 0));
        leftPanel.setOpaque(false);

        // Logo
        JLabel logoLabel = new JLabel();
        String logoPath = com.hust.soict.aims.utils.ImageUtils.getLogoPath();
        if (logoPath != null) {
            java.io.File logoFile = new java.io.File(logoPath);
            if (logoFile.exists()) {
                ImageIcon logoIcon = new ImageIcon(logoPath);
                Image logoImg = logoIcon.getImage();
                // Scale logo to fit header height
                int logoHeight = HEADER_HEIGHT - 10;
                int logoWidth = (int) (logoIcon.getIconWidth() * ((double) logoHeight / logoIcon.getIconHeight()));
                Image scaledLogo = logoImg.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledLogo));
            }
        }
        leftPanel.add(logoLabel);

        JLabel titleLabel = new JLabel("AIMS - Product Store");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        leftPanel.add(titleLabel);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        // Right: Cart and Login buttons (Cart first, Login second)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_SMALL, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(cartButton);
        rightPanel.add(loginButton);

        headerPanel.add(rightPanel, BorderLayout.EAST);

        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Center - Product grid with scroll (with padding)
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(BACKGROUND_LIGHT);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(0, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_LIGHT);
        scrollPane.getViewport().setBackground(BACKGROUND_LIGHT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        centerWrapper.add(scrollPane, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // Footer - Pagination (with padding)
        JPanel footerWrapper = new JPanel(new BorderLayout());
        footerWrapper.setBackground(BACKGROUND_LIGHT);
        footerWrapper.setBorder(BorderFactory.createEmptyBorder(0, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM));
        footerWrapper.add(paginationPanel, BorderLayout.CENTER);
        add(footerWrapper, BorderLayout.SOUTH);
    }

    @Override
    protected void bindEvents() {
        // Bind search events
        searchPanel.addSearchListener((searchTerm, category, minPrice, maxPrice) -> {
            currentSearchTerm = searchTerm;
            currentCategory = category;
            currentMinPrice = minPrice;
            currentMaxPrice = maxPrice;
            paginationPanel.reset();
            refresh();
        });

        // Bind pagination events
        paginationPanel.addPaginationListener(newPage -> {
            refresh();
        });

        // Bind login button
        loginButton.addActionListener(e -> {
            LoginScreen loginScreen = new LoginScreen(this, this);
            loginScreen.setVisible(true);
        });

        // Bind cart button
        cartButton.addActionListener(e -> {
            if (cartController.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Cart is empty",
                        "Cart",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                openCart();
            }
        });

        // Subscribe to cart changes to update cart button
        cartController.setChangeListener(count -> {
            SwingUtilities.invokeLater(() -> {
                cartButton.setText(getCartButtonText());
            });
        });
    }

    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        // Refresh product list every time the screen is
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        refresh();
    }

    /**
     * Refresh the product list and pagination display
     * Override from BaseScreenHandler to implement specific refresh logic
     */
    @Override
    public void refresh() {
        gridPanel.removeAll();

        // Get current page from pagination panel
        int currentPage = paginationPanel.getCurrentPage();

        // Load products with filters
        List<Product> products;
        int total;

        // Check if any filter is active
        boolean hasFilters = !currentSearchTerm.isEmpty() ||
                currentCategory != null ||
                currentMinPrice != null ||
                currentMaxPrice != null;

        if (hasFilters) {
            // Use filtered search
            products = productController.searchProductsWithFilters(
                    currentSearchTerm,
                    currentCategory,
                    currentMinPrice,
                    currentMaxPrice,
                    currentPage);
            total = productController.countFilteredResults(
                    currentSearchTerm,
                    currentCategory,
                    currentMinPrice,
                    currentMaxPrice);
        } else {
            // Load all products
            products = productController.getPage(currentPage);
            total = productController.countProducts();
        }

        // Show message if no results
        if (products.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No products found");
            noResultsLabel.setFont(FONT_HEADER);
            noResultsLabel.setForeground(TEXT_SECONDARY);
            noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gridPanel.setLayout(new BorderLayout());
            gridPanel.add(noResultsLabel, BorderLayout.CENTER);
        } else {
            // Restore grid layout if needed
            if (!(gridPanel.getLayout() instanceof GridLayout)) {
                gridPanel.setLayout(new GridLayout(PRODUCT_GRID_ROWS, PRODUCT_GRID_COLS,
                        PRODUCT_GRID_HGAP, PRODUCT_GRID_VGAP));
            }

            // Create product cards using ProductCardPanel component
            for (Product product : products) {
                ProductCardPanel card = new ProductCardPanel(product, cartController, this);

                // Set callback for info button
                card.setOnViewInfo(e -> {
                    ProductDetailScreen detailScreen = new ProductDetailScreen(this, product);
                    detailScreen.setVisible(true);
                });

                gridPanel.add(card);
            }
        }

        // Update pagination
        int totalPages = Math.max(1, (total + productController.getPageSize() - 1) / productController.getPageSize());
        paginationPanel.setCurrentPage(currentPage, totalPages);

        // Refresh UI
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Get cart button text with icon and item count
     * If count > 0, the count will be displayed in red color
     */
    private String getCartButtonText() {
        int count = cartController.getTotalQuantity();
        if (count > 0) {
            // Use HTML to display count in red color
            return String.format("<html>Cart <font color='#E74C3C'>(%d)</font></html>", count);
        } else {
            return "Cart (0)";
        }
    }

    /**
     * Open cart screen
     */
    private void openCart() {
        CartScreen cartScreen = new CartScreen(cartController, this);
        navigateTo(cartScreen);
    }
}
