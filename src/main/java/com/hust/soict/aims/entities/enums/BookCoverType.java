package com.hust.soict.aims.entities.enums;

/**
 * Enum for book cover type
 */
public enum BookCoverType {
    PAPERBACK("paperback"),
    HARDCOVER("hardcover");
    
    private final String value;
    
    BookCoverType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Get enum from string value
     */
    public static BookCoverType fromString(String value) {
        if (value == null) {
            return PAPERBACK; // Default
        }
        for (BookCoverType coverType : BookCoverType.values()) {
            if (coverType.value.equalsIgnoreCase(value)) {
                return coverType;
            }
        }
        return PAPERBACK; // Default
    }
    
    @Override
    public String toString() {
        return value;
    }
}

