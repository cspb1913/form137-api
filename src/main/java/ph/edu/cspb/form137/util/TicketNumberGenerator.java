package ph.edu.cspb.form137.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating unique ticket numbers for Form 137 requests.
 */
public class TicketNumberGenerator {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMddyyyy");
    
    /**
     * Generates a unique ticket number with the format: REQ-MMDDYYYY{last5digits}
     * where MMDDYYYY is the current date and {last5digits} are the last 5 digits of the timestamp.
     * 
     * @return A unique ticket number string
     */
    public static String generateTicketNumber() {
        Instant now = Instant.now();
        
        // Get the date portion in MMddyyyy format
        String datePart = now.atZone(ZoneId.systemDefault()).format(DATE_FORMAT);
        
        // Get timestamp in milliseconds and extract last 5 digits
        long timestamp = now.toEpochMilli();
        String timestampStr = String.valueOf(timestamp);
        String last5Digits = timestampStr.length() >= 5 ? 
            timestampStr.substring(timestampStr.length() - 5) : 
            String.format("%05d", Integer.parseInt(timestampStr));
        
        return "REQ-" + datePart + last5Digits;
    }
}