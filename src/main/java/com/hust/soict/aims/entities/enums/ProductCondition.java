package com.hust.soict.aims.entities.enums;

/**
 * Enum for product condition
 */
public enum ProductCondition {
    NEW("new"),
    USED("used");
    
    private final String value;
    
    ProductCondition(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Get enum from string value
     */
    public static ProductCondition fromString(String value) {
        if (value == null) {
            return NEW; // Default
        }
        for (ProductCondition condition : ProductCondition.values()) {
            if (condition.value.equalsIgnoreCase(value)) {
                return condition;
            }
        }
        return NEW; // Default
    }
    
    @Override
    public String toString() {
        return value;
    }
}

