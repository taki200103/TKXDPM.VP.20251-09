package com.hust.soict.aims.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.border.EmptyBorder;

public class UIConstant {
    // ========== COLOR PALETTE ==========
    
    // Primary Colors
    public static final Color PRIMARY_COLOR = new Color(52, 152, 219);      // #3498DB - Blue
    public static final Color PRIMARY_DARK = new Color(41, 128, 185);       // #2980B9 - Dark Blue
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;
    
    // Secondary Colors
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // #2ECC71 - Green
    public static final Color SUCCESS_DARK = new Color(39, 174, 96);        // #27AE60 - Dark Green
    public static final Color DANGER_COLOR = new Color(231, 76, 60);        // #E74C3C - Red
    public static final Color DANGER_DARK = new Color(192, 57, 43);         // #C0392B - Dark Red
    public static final Color WARNING_COLOR = new Color(243, 156, 18);      // #F39C12 - Orange
    public static final Color INFO_COLOR = new Color(52, 152, 219);         // #3498DB - Blue
    
    // Neutral Colors - Background
    public static final Color BACKGROUND_WHITE = Color.WHITE;
    public static final Color BACKGROUND_LIGHT = new Color(245, 245, 245);  // #F5F5F5
    public static final Color BACKGROUND_GRAY = new Color(236, 240, 241);   // #ECF0F1
    
    // Neutral Colors - Text
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);         // #212121
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);    // #757575
    public static final Color TEXT_DISABLED = new Color(189, 189, 189);     // #BDBDBD
    
    // Neutral Colors - Border
    public static final Color BORDER_LIGHT = new Color(220, 220, 220);      // #DCDCDC
    public static final Color BORDER_MEDIUM = new Color(200, 200, 200);     // #C8C8C8
    public static final Color BORDER_DARK = new Color(149, 165, 166);       // #95A5A6
    
    // ========== TYPOGRAPHY ==========
    
    // Font Family
    public static final String FONT_FAMILY = "Arial";
    
    // Font Sizes
    public static final int FONT_SIZE_TITLE = 24;          // Screen titles
    public static final int FONT_SIZE_HEADER = 18;         // Section headers
    public static final int FONT_SIZE_BUTTON_LARGE = 16;   // Large buttons
    public static final int FONT_SIZE_PRODUCT_NAME = 15;   // Product names
    public static final int FONT_SIZE_BODY = 14;           // Normal text, body
    public static final int FONT_SIZE_BUTTON = 13;         // Regular buttons
    public static final int FONT_SIZE_SMALL = 13;          // Secondary text
    public static final int FONT_SIZE_CAPTION = 12;        // Very small text
    
    // Font Objects
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_TITLE);
    public static final Font FONT_HEADER = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_HEADER);
    public static final Font FONT_BUTTON_LARGE = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_BUTTON_LARGE);
    public static final Font FONT_PRODUCT_NAME = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_PRODUCT_NAME);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_BODY);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_BUTTON);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_SMALL);
    public static final Font FONT_CAPTION = new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_CAPTION);
    
    // ========== SPACING ==========
    
    // Standard spacing units
    public static final int SPACING_XSMALL = 5;
    public static final int SPACING_SMALL = 10;
    public static final int SPACING_MEDIUM = 15;
    public static final int SPACING_LARGE = 20;
    public static final int SPACING_XLARGE = 30;
    
    // Border padding
    public static final EmptyBorder PADDING_XSMALL = new EmptyBorder(5, 5, 5, 5);
    public static final EmptyBorder PADDING_SMALL = new EmptyBorder(10, 10, 10, 10);
    public static final EmptyBorder PADDING_MEDIUM = new EmptyBorder(15, 15, 15, 15);
    public static final EmptyBorder PADDING_LARGE = new EmptyBorder(20, 20, 20, 20);
    
    // ========== DIMENSIONS ==========
    
    // Button Sizes
    public static final Dimension BUTTON_SIZE_LARGE = new Dimension(200, 50);
    public static final Dimension BUTTON_SIZE_MEDIUM = new Dimension(110, 35);
    public static final Dimension BUTTON_SIZE_SMALL = new Dimension(80, 35);
    public static final Dimension BUTTON_SIZE_ICON = new Dimension(45, 35);
    
    // Input Sizes
    public static final Dimension INPUT_SIZE_SMALL = new Dimension(70, 35);
    public static final Dimension INPUT_SIZE_MEDIUM = new Dimension(150, 35);
    public static final Dimension INPUT_SIZE_LARGE = new Dimension(250, 35);
    
    // Window Sizes
    public static final int DEFAULT_SCREEN_WIDTH = 1000;
    public static final int DEFAULT_SCREEN_HEIGHT = 800;
    public static final Dimension WINDOW_SIZE_DEFAULT = new Dimension(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
    public static final Dimension WINDOW_SIZE_DIALOG = new Dimension(600, 500);
    public static final Dimension WINDOW_SIZE_SMALL_DIALOG = new Dimension(400, 300);
    
    // Header Sizes
    public static final int HEADER_HEIGHT = 60;
    public static final int TOP_NAV_HEIGHT = 40;
    
    // Cart Item Card Dimensions
    public static final int CART_ITEM_HEIGHT = 80;
    public static final int CART_PRODUCT_WIDTH = 400;
    public static final int CART_QUANTITY_WIDTH = 120;
    public static final int CART_ACTIONS_WIDTH = 180;
    
    // ========== CURSORS ==========
    
    public static final Cursor CURSOR_HAND = new Cursor(Cursor.HAND_CURSOR);
    public static final Cursor CURSOR_DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);
    
    // ========== GRID LAYOUT ==========
    
    public static final int PRODUCT_GRID_ROWS = 5;
    public static final int PRODUCT_GRID_COLS = 4;
    public static final int PRODUCT_GRID_HGAP = 10;
    public static final int PRODUCT_GRID_VGAP = 10;
    
    // ========== PRIVATE CONSTRUCTOR ==========
    
    /**
     * Private constructor to prevent instantiation of utility class
     */
    private UIConstant() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
