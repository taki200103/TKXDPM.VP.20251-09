package com.hust.soict.aims.boundaries.manager;

import javax.swing.*;
import java.awt.*;
import com.hust.soict.aims.boundaries.BaseScreenHandler;
import com.hust.soict.aims.boundaries.customer.homepage.Homepage;
import com.hust.soict.aims.entities.Product;
import com.hust.soict.aims.utils.RoundedButton;
import com.hust.soict.aims.utils.ImageUtils;
import static com.hust.soict.aims.utils.UIConstant.*;

/**
 * Main Manager Screen with Sidebar and Header
 * 
 * Layout:
 * - Sidebar (left): Menu items (currently only "Product Management")
 * - Header (top): Logo and Logout button
 * - Content (center): Dynamic content based on selected menu item
 */
public class ManagerMainScreen extends BaseScreenHandler {
    private JPanel sidebarPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    ProductManagementScreen productManagementScreen; // Package-private for access from ProductFormScreen
    private ProductFormScreen currentProductFormScreen;
    
    public ManagerMainScreen(BaseScreenHandler parent) {
        super("Manager Dashboard", parent, false);
        initializeScreen();
    }
    
    @Override
    protected void initComponents() {
        // Initialize sidebar
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(PRIMARY_COLOR); // Orange sidebar to match theme
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_MEDIUM, 0, SPACING_MEDIUM, 0));
        
        // Initialize header
        headerPanel = new JPanel(new BorderLayout(SPACING_MEDIUM, 0));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_MEDIUM, SPACING_LARGE, SPACING_MEDIUM, SPACING_LARGE));
        headerPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT + 10));
        
        // Initialize content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_LIGHT);
        
        // Initialize product management screen (will be embedded in content panel)
        productManagementScreen = new ProductManagementScreen(this);
    }
    
    @Override
    protected void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_LIGHT);
        
        // Setup Sidebar
        setupSidebar();
        
        // Setup Header
        setupHeader();
        
        // Setup Content
        setupContent();
        
        // Add components to main layout
        add(sidebarPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Setup sidebar with menu items
     */
    private void setupSidebar() {
        // Menu item: Product Management
        JButton productMenuButton = createMenuButton("Product Management");
        productMenuButton.addActionListener(e -> showProductManagement());
        sidebarPanel.add(productMenuButton);
        
        // Add spacing at bottom
        sidebarPanel.add(Box.createVerticalGlue());
    }
    
    /**
     * Create a menu button for sidebar
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(TEXT_ON_PRIMARY);
        button.setBackground(PRIMARY_DARK); // Darker orange for button
        button.setBorder(BorderFactory.createEmptyBorder(SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setCursor(CURSOR_HAND);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        // Hover effect - lighter orange on hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(
                    Math.min(255, PRIMARY_COLOR.getRed() + 20),
                    Math.min(255, PRIMARY_COLOR.getGreen() + 20),
                    Math.min(255, PRIMARY_COLOR.getBlue() + 20)
                ));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_DARK);
            }
        });
        
        return button;
    }
    
    /**
     * Setup header with logo and logout button
     */
    private void setupHeader() {
        // Left: Logo
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_SMALL, 0));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel();
        String logoPath = ImageUtils.getLogoPath();
        if (logoPath != null) {
            java.io.File logoFile = new java.io.File(logoPath);
            if (logoFile.exists()) {
                ImageIcon logoIcon = new ImageIcon(logoPath);
                Image logoImg = logoIcon.getImage();
                int logoHeight = HEADER_HEIGHT - 10;
                int logoWidth = (int) (logoIcon.getIconWidth() * ((double) logoHeight / logoIcon.getIconHeight()));
                Image scaledLogo = logoImg.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledLogo));
            }
        }
        leftPanel.add(logoLabel);
        
        JLabel titleLabel = new JLabel("Manager Dashboard");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_ON_PRIMARY);
        leftPanel.add(titleLabel);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        
        // Right: Logout button
        RoundedButton logoutButton = new RoundedButton("Logout", 8);
        logoutButton.setFont(FONT_BUTTON);
        logoutButton.setBackground(new Color(255, 255, 255, 30)); // Semi-transparent white
        logoutButton.setForeground(TEXT_ON_PRIMARY);
        logoutButton.setCursor(CURSOR_HAND);
        logoutButton.setPreferredSize(new Dimension(100, 40));
        logoutButton.addActionListener(e -> performLogout());
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(logoutButton);
        
        headerPanel.add(rightPanel, BorderLayout.EAST);
    }
    
    /**
     * Setup content area
     */
    private void setupContent() {
        // Show product management by default
        showProductManagement();
    }
    
    /**
     * Show product management screen in content area
     */
    public void showProductManagement() {
        contentPanel.removeAll();
        // Get the content from ProductManagementScreen (it's a JFrame, so we get its content pane)
        JPanel productPanel = (JPanel) productManagementScreen.getContentPane();
        productPanel.setBorder(null); // Remove any borders
        contentPanel.add(productPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Show product form screen in content area
     */
    public void showProductForm(Product product) {
        contentPanel.removeAll();
        try {
            // Create new ProductFormScreen
            currentProductFormScreen = new ProductFormScreen(this, product);
            // Get the content from ProductFormScreen (it's a JFrame, so we get its content pane)
            Container formContainer = currentProductFormScreen.getContentPane();
            
            // Remove the form from its original parent (JFrame) and add to contentPanel
            if (formContainer.getParent() != null) {
                formContainer.getParent().remove(formContainer);
            }
            
            // Simply add to contentPanel - don't modify layout
            contentPanel.add(formContainer, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error showing product form: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Perform logout and return to homepage
     */
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Navigate back to homepage
            BaseScreenHandler parent = getParentScreen();
            if (parent != null) {
                navigateTo(parent);
            } else {
                // If no parent, create a new homepage
                com.hust.soict.aims.controls.ProductController productController = 
                    new com.hust.soict.aims.controls.ProductController();
                com.hust.soict.aims.controls.CartController cartController = 
                    new com.hust.soict.aims.controls.CartController();
                Homepage homepage = new Homepage(productController, cartController);
                navigateTo(homepage);
            }
        }
    }
    
    @Override
    protected void bindEvents() {
        // Events are bound in individual components
    }
    
    @Override
    protected void onBeforeShow() {
        super.onBeforeShow();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        if (productManagementScreen != null) {
            productManagementScreen.refresh();
        }
    }
    
    @Override
    public void refresh() {
        if (productManagementScreen != null) {
            productManagementScreen.refresh();
        }
    }
}
