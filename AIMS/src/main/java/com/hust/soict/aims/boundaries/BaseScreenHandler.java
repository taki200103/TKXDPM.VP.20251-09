package com.hust.soict.aims.boundaries;

import javax.swing.*;
import java.awt.*;

import static com.hust.soict.aims.utils.UIConstant.*;

public abstract class BaseScreenHandler extends JFrame {
    protected String screenTitle;
    protected BaseScreenHandler parentScreen;
    
    // Navigation buttons
    private JButton backButton;
    private JButton forwardButton;
    private JPanel navigationPanel;
    private boolean navigationEnabled = true;
     
    /**
     * Constructor with screen's title
     * @param title screen's title
     */
    public BaseScreenHandler(String title) {
        this(title, null);
    }
    
    /**
     * Constructor with screen's title and parent screen
     * @param title screen's title
     * @param parent parent screen
     */
    public BaseScreenHandler(String title, BaseScreenHandler parent) {
        this(title, parent, true);
    }
    
    /**
     * Constructor with screen's title, parent screen, and auto-initialize flag
     * This constructor allows subclass to delay initialization until after setting dependencies
     * 
     * @param title screen's title
     * @param parent parent screen
     * @param autoInitialize if true, automatically call initializeScreen(); if false, subclass must call it manually
     */
    protected BaseScreenHandler(String title, BaseScreenHandler parent, boolean autoInitialize) {
        this.screenTitle = title;
        this.parentScreen = parent;
        
        if (autoInitialize) {
            initializeScreen();
        }
    }
    
    /**
     * Initialize the basic settings for the screen
     * Automatically called in the constructor
     */
    protected void initializeScreen() {
        setTitle(screenTitle);
        setSize(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Lifecycle hook: before initialization
        onBeforeInitialize();
        
        // Initialize navigation buttons first
        if (navigationEnabled) {
            initNavigationComponents();
        }
        
        // Template methods for subclass to implement
        initComponents();
        setupLayout();
        bindEvents();
        
        // Setup navigation listener
        if (navigationEnabled) {
            setupNavigationListener();
        }
        
        // Center the screen
        setLocationRelativeTo(null);
        
        // Lifecycle hook: after initialization
        onAfterInitialize();
    }
    
    /**
     * Initialize navigation components (Back/Forward buttons)
     */
    private void initNavigationComponents() {
        backButton = new JButton("← Back");
        backButton.setFont(FONT_BUTTON);
        backButton.setBackground(BACKGROUND_GRAY);
        backButton.setFocusPainted(false);
        backButton.setCursor(CURSOR_HAND);
        backButton.setEnabled(false);
        
        forwardButton = new JButton("Forward →");
        forwardButton.setFont(FONT_BUTTON);
        forwardButton.setBackground(BACKGROUND_GRAY);
        forwardButton.setFocusPainted(false);
        forwardButton.setCursor(CURSOR_HAND);
        forwardButton.setEnabled(false);
        
        // Bind navigation events
        backButton.addActionListener(e -> navigateBack());
        forwardButton.addActionListener(e -> navigateForward());
        
        // Create navigation panel
        navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_SMALL, 0));
        navigationPanel.setOpaque(false);
        navigationPanel.add(backButton);
        navigationPanel.add(forwardButton);
    }
    
    /**
     * Setup navigation listener to update button states
     */
    private void setupNavigationListener() {
        ScreenNavigator.getInstance().addNavigationListener((currentScreen, canGoBack, canGoForward) -> {
            // Only update if this is the current screen
            if (currentScreen == this) {
                SwingUtilities.invokeLater(() -> {
                    backButton.setEnabled(canGoBack);
                    forwardButton.setEnabled(canGoForward);
                });
            }
        });
    }
    
    /**
     * Get the navigation panel to add to screen layout
     * Subclasses can call this to include navigation buttons in their header
     * @return Navigation panel with Back/Forward buttons
     */
    protected JPanel getNavigationPanel() {
        return navigationPanel;
    }
    
    /**
     * Create a top navigation bar (separate row for Back/Forward buttons)
     * This creates a visually separated top bar for navigation controls
     * @return Top navigation bar panel
     */
    protected JPanel createTopNavigationBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BACKGROUND_GRAY);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
            PADDING_XSMALL
        ));
        topBar.setPreferredSize(new Dimension(0, TOP_NAV_HEIGHT));
        
        topBar.add(navigationPanel, BorderLayout.WEST);
        
        return topBar;
    }
    
    /**
     * Helper method to create a standard header structure with top navigation bar and main header
     * @param mainHeaderContent The main header content (title, buttons, etc.)
     * @return Panel containing top navigation bar and main header
     */
    protected JPanel createHeaderWithNavigation(JPanel mainHeaderContent) {
        JPanel headerWrapper = new JPanel(new BorderLayout());
        
        // Top: Navigation bar
        headerWrapper.add(createTopNavigationBar(), BorderLayout.NORTH);
        
        // Bottom: Main header content
        headerWrapper.add(mainHeaderContent, BorderLayout.CENTER);
        
        return headerWrapper;
    }
    
    /**
     * Enable or disable navigation buttons for this screen
     * By default, navigation is enabled. Call this in constructor before initializeScreen() to disable.
     * @param enabled true to enable navigation, false to disable
     */
    protected void setNavigationEnabled(boolean enabled) {
        this.navigationEnabled = enabled;
    }
    
    /**
     * Check if navigation is enabled for this screen
     * @return true if navigation enabled, false otherwise
     */
    protected boolean isNavigationEnabled() {
        return navigationEnabled;
    }
    
    /**
     * Template method: Initialize the components of the screen
     * Subclass must override to create the specific components
     */
    protected abstract void initComponents();
    
    /**
     * Template method: Setup the layout for the screen
     * Subclass must override to setup the specific layout
     */
    protected abstract void setupLayout();
    
    /**
     * Template method: Bind events for the components
     * Subclass can override to bind events (optional)
     */
    protected void bindEvents() {
        // Default: do nothing, subclass can override if needed
    }
    
    /**
     * Refresh/update the interface of the screen
     * Subclass can override to implement the specific refresh logic
     */
    public void refresh() {
        revalidate();
        repaint();
    }
    
    /**
     * Show the screen with lifecycle hooks
     * Note: Do NOT override Component.show() as it causes infinite loop with setVisible()
     */
    public void showScreen() {
        onBeforeShow();
        setVisible(true);
        onAfterShow();
    }
    
    /**
     * Hide the screen
     * Note: Do NOT override Component.hide() as it causes infinite loop with setVisible()
     */
    public void hideScreen() {
        setVisible(false);
    }
    
    /**
     * Close the screen and return to the parent screen (if any)
     */
    public void closeAndReturnToParent() {
        onBeforeClose();
        dispose();
        if (parentScreen != null) {
            parentScreen.showScreen();
        }
    }
    
    /**
     * Navigate to another screen using ScreenNavigator
     * This will automatically manage the navigation history
     * @param screen The screen to navigate to
     */
    protected void navigateTo(BaseScreenHandler screen) {
        ScreenNavigator.getInstance().navigateTo(screen);
    }
    
    /**
     * Go back to previous screen in navigation history
     * @return true if successfully went back, false if no previous screen
     */
    protected boolean navigateBack() {
        return ScreenNavigator.getInstance().goBack();
    }
    
    /**
     * Go forward to next screen in navigation history
     * @return true if successfully went forward, false if no next screen
     */
    protected boolean navigateForward() {
        return ScreenNavigator.getInstance().goForward();
    }
    
    /**
     * Check if can navigate back
     * @return true if there's a previous screen
     */
    protected boolean canNavigateBack() {
        return ScreenNavigator.getInstance().canGoBack();
    }
    
    /**
     * Check if can navigate forward
     * @return true if there's a next screen
     */
    protected boolean canNavigateForward() {
        return ScreenNavigator.getInstance().canGoForward();
    }
    
    /**
     * Get the navigation instance for advanced usage
     * @return ScreenNavigator instance
     */
    protected ScreenNavigator getNavigator() {
        return ScreenNavigator.getInstance();
    }

    /**
     * Lifecycle hook: called before initializing the components
     * Subclass can override to implement the logic before initializing
     */
    protected void onBeforeInitialize() {}
    
    /**
     * Lifecycle hook: called after initializing the components finished
     * Subclass can override to implement the logic after initializing
     */
    protected void onAfterInitialize() {}
    
    /**
     * Lifecycle hook: called before showing the screen
     * Subclass can override to implement the logic before showing
     */
    protected void onBeforeShow() {}
    
    /**
     * Lifecycle hook: called after showing the screen
     * Subclass can override to implement the logic after showing
     */
    protected void onAfterShow() {}
    
    /**
     * Lifecycle hook: called before closing the screen
     * Subclass can override to implement the logic before closing
     */
    protected void onBeforeClose() {}
    
    public String getScreenTitle() {
        return screenTitle;
    }
    
    public void setScreenTitle(String screenTitle) {
        this.screenTitle = screenTitle;
        setTitle(screenTitle);
    }
    
    public BaseScreenHandler getParentScreen() {
        return parentScreen;
    }
    
    public void setParentScreen(BaseScreenHandler parentScreen) {
        this.parentScreen = parentScreen;
    }
}
