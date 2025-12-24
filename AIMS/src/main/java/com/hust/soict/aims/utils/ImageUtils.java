package com.hust.soict.aims.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Utility class for handling product images and common images
 */
public class ImageUtils {
    private static final String IMAGES_FOLDER = "src/main/resources/images/products";
    private static final String COMMON_IMAGES_FOLDER = "src/main/resources/images/common";
    private static final String IMAGE_EXTENSION = ".jpg";
    
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
    
    /**
     * Get logo path from common images folder
     * @param logoFileName Logo file name (e.g., "logo.png")
     * @return Full path to the logo file, or null if doesn't exist
     */
    public static String getLogoPath(String logoFileName) {
        String logoPath = COMMON_IMAGES_FOLDER + File.separator + logoFileName;
        File logoFile = new File(logoPath);
        if (logoFile.exists()) {
            return logoPath;
        }
        return null;
    }
    
    /**
     * Get logo path (tries common names: logo.png, logo.jpg, etc.)
     * @return Full path to the logo file, or null if doesn't exist
     */
    public static String getLogoPath() {
        String[] commonNames = {"logo.png", "logo.jpg", "logo.jpeg", "logo.gif"};
        for (String name : commonNames) {
            String path = getLogoPath(name);
            if (path != null) {
                return path;
            }
        }
        return null;
    }
    
    /**
     * Get the common images folder path
     * @return Path to common images folder
     */
    public static String getCommonImagesFolder() {
        return COMMON_IMAGES_FOLDER;
    }
    
    /**
     * Save an image file to the products folder with the specified product ID
     * @param sourceFile Source image file to copy
     * @param productId Product ID to use as filename
     * @return true if successful, false otherwise
     */
    public static boolean saveProductImage(File sourceFile, long productId) {
        if (sourceFile == null || !sourceFile.exists()) {
            return false;
        }
        
        try {
            // Ensure the images folder exists
            File imagesFolder = new File(IMAGES_FOLDER);
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }
            
            // Create target file path
            String targetPath = getProductImagePathAlways(productId);
            File targetFile = new File(targetPath);
            
            // Copy the source file to target location
            try (FileInputStream fis = new FileInputStream(sourceFile);
                 FileOutputStream fos = new FileOutputStream(targetFile)) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
