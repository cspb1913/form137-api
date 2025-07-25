package ph.edu.cspb.form137.model;

/**
 * Enumeration representing the valid status values for Form 137 requests.
 * Values are in lowercase to match the database representation.
 */
public enum Form137RequestStatus {
    /**
     * Request has been submitted but not yet reviewed.
     */
    PENDING("pending"),
    
    /**
     * Request is being processed by staff.
     */
    PROCESSING("processing"),
    
    /**
     * Request has been completed and documents are ready.
     */
    COMPLETED("completed"),
    
    /**
     * Request has been rejected due to incomplete information or other issues.
     */
    REJECTED("rejected"),
    
    /**
     * Request has been cancelled by the requester or administrator.
     */
    CANCELLED("cancelled");
    
    private final String value;
    
    Form137RequestStatus(String value) {
        this.value = value;
    }
    
    /**
     * Get the string value as stored in the database.
     * @return the lowercase string value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Convert a string value to a Form137RequestStatus enum.
     * Case-insensitive matching.
     * 
     * @param value the string value to convert
     * @return the corresponding enum value
     * @throws IllegalArgumentException if the value is not valid
     */
    public static Form137RequestStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Status value cannot be null or empty");
        }
        
        String normalizedValue = value.trim().toLowerCase();
        for (Form137RequestStatus status : values()) {
            if (status.getValue().equals(normalizedValue)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Invalid status value: " + value + 
            ". Valid values are: " + getValidValuesString());
    }
    
    /**
     * Check if a string value is a valid status.
     * Case-insensitive matching.
     * 
     * @param value the string value to check
     * @return true if the value is valid, false otherwise
     */
    public static boolean isValid(String value) {
        try {
            fromValue(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Get a comma-separated string of all valid status values.
     * @return string of valid values for error messages
     */
    public static String getValidValuesString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values().length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(values()[i].getValue());
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return value;
    }
}