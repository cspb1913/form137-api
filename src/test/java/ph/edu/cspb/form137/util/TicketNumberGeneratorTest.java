package ph.edu.cspb.form137.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.HashSet;

class TicketNumberGeneratorTest {

    @Test
    void testTicketNumberFormat() {
        String ticketNumber = TicketNumberGenerator.generateTicketNumber();
        
        // Should start with "REQ-"
        assertTrue(ticketNumber.startsWith("REQ-"));
        
        // Should have the correct length: REQ- (4) + MMddyyyy (8) + last5digits (5) = 17
        assertEquals(17, ticketNumber.length());
        
        // Extract the date part and verify it matches today's date
        String datePart = ticketNumber.substring(4, 12); // characters after "REQ-"
        String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMddyyyy"));
        assertEquals(expectedDate, datePart);
        
        // Last 5 characters should be digits
        String last5 = ticketNumber.substring(12);
        assertTrue(last5.matches("\\d{5}"));
    }
    
    @Test
    void testUniqueness() {
        Set<String> generatedNumbers = new HashSet<>();
        
        // Generate multiple ticket numbers in quick succession
        for (int i = 0; i < 100; i++) {
            String ticketNumber = TicketNumberGenerator.generateTicketNumber();
            assertFalse(generatedNumbers.contains(ticketNumber), 
                "Duplicate ticket number generated: " + ticketNumber);
            generatedNumbers.add(ticketNumber);
            
            // Small delay to ensure different timestamps
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Test
    void testTicketNumberPattern() {
        String ticketNumber = TicketNumberGenerator.generateTicketNumber();
        
        // Should match the pattern REQ-MMddyyyyDDDDD where D are digits
        assertTrue(ticketNumber.matches("REQ-\\d{8}\\d{5}"));
    }
}