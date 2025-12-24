package com.hust.soict.aims.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating barcode images
 */
public class BarcodeGenerator {
    
    /**
     * Generate a barcode image from a string
     * @param barcodeText The text to encode as barcode
     * @param width Width of the barcode image
     * @param height Height of the barcode image
     * @return BufferedImage of the barcode, or null if generation fails
     */
    public static BufferedImage generateBarcodeImage(String barcodeText, int width, int height) {
        if (barcodeText == null || barcodeText.trim().isEmpty()) {
            return null;
        }
        
        try {
            Code128Writer writer = new Code128Writer();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 2); // Add margin around barcode
            
            // Encode barcode
            BitMatrix bitMatrix = writer.encode(barcodeText, BarcodeFormat.CODE_128, width, height, hints);
            
            // Convert BitMatrix to BufferedImage using MatrixToImageWriter
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (Exception e) {
            // Catch all exceptions (WriterException is checked but compiler may not detect it)
            System.err.println("Error generating barcode: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Generate a barcode image with default size (300x80)
     * @param barcodeText The text to encode as barcode
     * @return BufferedImage of the barcode, or null if generation fails
     */
    public static BufferedImage generateBarcodeImage(String barcodeText) {
        return generateBarcodeImage(barcodeText, 300, 80);
    }
    
    /**
     * Generate a barcode image with text label below
     * @param barcodeText The text to encode as barcode
     * @param width Width of the barcode image
     * @param height Height of the barcode image (excluding text)
     * @param showText Whether to show the barcode text below the barcode
     * @return BufferedImage of the barcode with optional text, or null if generation fails
     */
    public static BufferedImage generateBarcodeImageWithText(String barcodeText, int width, int height, boolean showText) {
        if (barcodeText == null || barcodeText.trim().isEmpty()) {
            return null;
        }
        
        BufferedImage barcodeImage = generateBarcodeImage(barcodeText, width, height);
        if (barcodeImage == null) {
            return null;
        }
        
        if (!showText) {
            return barcodeImage;
        }
        
        // Create image with text below
        int textHeight = 20;
        int totalHeight = height + textHeight;
        BufferedImage imageWithText = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imageWithText.createGraphics();
        
        // Enable anti-aliasing for better text rendering
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw white background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, totalHeight);
        
        // Draw barcode
        g.drawImage(barcodeImage, 0, 0, null);
        
        // Draw text below barcode
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(barcodeText);
        int textX = (width - textWidth) / 2;
        int textY = height + 15;
        g.drawString(barcodeText, textX, textY);
        
        g.dispose();
        return imageWithText;
    }
    
    /**
     * Generate a barcode image with text label (default size)
     * @param barcodeText The text to encode as barcode
     * @param showText Whether to show the barcode text below the barcode
     * @return BufferedImage of the barcode with optional text, or null if generation fails
     */
    public static BufferedImage generateBarcodeImageWithText(String barcodeText, boolean showText) {
        return generateBarcodeImageWithText(barcodeText, 300, 80, showText);
    }
}
