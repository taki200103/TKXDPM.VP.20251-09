package com.hust.soict.aims.entities.enums;

/**
 * Enum for product status
 */
public enum ProductStatus {
    ACTIVE("active"),
    DEACTIVATED("deactivated");
    
    private final String value;
    
    ProductStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Get enum from string value
     */
    public static ProductStatus fromString(String value) {
        if (value == null) {
            return ACTIVE; // Default
        }
        for (ProductStatus status : ProductStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return ACTIVE; // Default
    }
    
    @Override
    public String toString() {
        return value;
    }
}

