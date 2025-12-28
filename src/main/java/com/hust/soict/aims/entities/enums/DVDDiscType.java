package com.hust.soict.aims.entities.enums;

/**
 * Enum for DVD disc type
 */
public enum DVDDiscType {
    BLU_RAY("Blu-ray"),
    HD_DVD("HD-DVD");
    
    private final String value;
    
    DVDDiscType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Get enum from string value
     */
    public static DVDDiscType fromString(String value) {
        if (value == null) {
            return BLU_RAY; // Default
        }
        for (DVDDiscType discType : DVDDiscType.values()) {
            if (discType.value.equalsIgnoreCase(value)) {
                return discType;
            }
        }
        return BLU_RAY; // Default
    }
    
    @Override
    public String toString() {
        return value;
    }
}

