package com.hust.soict.aims.utils;

import java.io.File;

/**
 * Utility class for handling product images
 */
public class ImageUtils {
    private static final String IMAGES_FOLDER = "src/main/resources/images/products";
    private static final String IMAGE_EXTENSION = ".png";
    
    /**
     * Get image path for a product based on its ID
     * @param productId Product ID
     * @return Full path to the image file, or null if image doesn't exist
     */
    public static String getProductImagePath(long productId) {
        String imageName = productId + IMAGE_EXTENSION;
        String imagePath = IMAGES_FOLDER + File.separator + imageName;
        
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            return imagePath;
        }
        
        return null;
    }
    
    /**
     * Get image path for a product based on its ID (returns path even if file doesn't exist)
     * Useful for setting default imagePath in database
     * @param productId Product ID
     * @return Full path to the image file
     */
    public static String getProductImagePathAlways(long productId) {
        String imageName = productId + IMAGE_EXTENSION;
        return IMAGES_FOLDER + File.separator + imageName;
    }
    
    /**
     * Check if product image exists
     * @param productId Product ID
     * @return true if image file exists, false otherwise
     */
    public static boolean productImageExists(long productId) {
        String imagePath = getProductImagePathAlways(productId);
        File imageFile = new File(imagePath);
        return imageFile.exists();
    }
    
    /**
     * Get the images folder path
     * @return Path to images folder
     */
    public static String getImagesFolder() {
        return IMAGES_FOLDER;
    }
}
