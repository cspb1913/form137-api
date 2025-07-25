package ph.edu.cspb.form137.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Form137RequestStatus enum.
 */
public class Form137RequestStatusTest {

    @Test
    public void testValidStatusValues() {
        // Test all valid lowercase values
        assertEquals("pending", Form137RequestStatus.PENDING.getValue());
        assertEquals("processing", Form137RequestStatus.PROCESSING.getValue());
        assertEquals("completed", Form137RequestStatus.COMPLETED.getValue());
        assertEquals("rejected", Form137RequestStatus.REJECTED.getValue());
        assertEquals("cancelled", Form137RequestStatus.CANCELLED.getValue());
    }

    @Test
    public void testFromValueWithValidLowercaseInput() {
        assertEquals(Form137RequestStatus.PENDING, Form137RequestStatus.fromValue("pending"));
        assertEquals(Form137RequestStatus.PROCESSING, Form137RequestStatus.fromValue("processing"));
        assertEquals(Form137RequestStatus.COMPLETED, Form137RequestStatus.fromValue("completed"));
        assertEquals(Form137RequestStatus.REJECTED, Form137RequestStatus.fromValue("rejected"));
        assertEquals(Form137RequestStatus.CANCELLED, Form137RequestStatus.fromValue("cancelled"));
    }

    @Test
    public void testFromValueWithValidUppercaseInput() {
        assertEquals(Form137RequestStatus.PENDING, Form137RequestStatus.fromValue("PENDING"));
        assertEquals(Form137RequestStatus.PROCESSING, Form137RequestStatus.fromValue("PROCESSING"));
        assertEquals(Form137RequestStatus.COMPLETED, Form137RequestStatus.fromValue("COMPLETED"));
        assertEquals(Form137RequestStatus.REJECTED, Form137RequestStatus.fromValue("REJECTED"));
        assertEquals(Form137RequestStatus.CANCELLED, Form137RequestStatus.fromValue("CANCELLED"));
    }

    @Test
    public void testFromValueWithMixedCaseInput() {
        assertEquals(Form137RequestStatus.PENDING, Form137RequestStatus.fromValue("Pending"));
        assertEquals(Form137RequestStatus.PROCESSING, Form137RequestStatus.fromValue("Processing"));
        assertEquals(Form137RequestStatus.COMPLETED, Form137RequestStatus.fromValue("CompLeTed"));
    }

    @Test
    public void testFromValueWithWhitespace() {
        assertEquals(Form137RequestStatus.PENDING, Form137RequestStatus.fromValue("  pending  "));
        assertEquals(Form137RequestStatus.PROCESSING, Form137RequestStatus.fromValue("\tprocessing\n"));
    }

    @Test
    public void testFromValueWithInvalidInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Form137RequestStatus.fromValue("invalid");
        });
        assertTrue(exception.getMessage().contains("Invalid status value"));
        assertTrue(exception.getMessage().contains("pending, processing, completed, rejected, cancelled"));
    }

    @Test
    public void testFromValueWithNullInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Form137RequestStatus.fromValue(null);
        });
        assertTrue(exception.getMessage().contains("Status value cannot be null or empty"));
    }

    @Test
    public void testFromValueWithEmptyInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Form137RequestStatus.fromValue("");
        });
        assertTrue(exception.getMessage().contains("Status value cannot be null or empty"));
    }

    @Test
    public void testFromValueWithWhitespaceOnlyInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Form137RequestStatus.fromValue("   ");
        });
        assertTrue(exception.getMessage().contains("Status value cannot be null or empty"));
    }

    @Test
    public void testIsValidWithValidValues() {
        assertTrue(Form137RequestStatus.isValid("pending"));
        assertTrue(Form137RequestStatus.isValid("PROCESSING"));
        assertTrue(Form137RequestStatus.isValid("  completed  "));
        assertTrue(Form137RequestStatus.isValid("Rejected"));
        assertTrue(Form137RequestStatus.isValid("CANCELLED"));
    }

    @Test
    public void testIsValidWithInvalidValues() {
        assertFalse(Form137RequestStatus.isValid("invalid"));
        assertFalse(Form137RequestStatus.isValid(null));
        assertFalse(Form137RequestStatus.isValid(""));
        assertFalse(Form137RequestStatus.isValid("   "));
    }

    @Test
    public void testGetValidValuesString() {
        String validValues = Form137RequestStatus.getValidValuesString();
        assertEquals("pending, processing, completed, rejected, cancelled", validValues);
    }

    @Test
    public void testToString() {
        assertEquals("pending", Form137RequestStatus.PENDING.toString());
        assertEquals("processing", Form137RequestStatus.PROCESSING.toString());
        assertEquals("completed", Form137RequestStatus.COMPLETED.toString());
        assertEquals("rejected", Form137RequestStatus.REJECTED.toString());
        assertEquals("cancelled", Form137RequestStatus.CANCELLED.toString());
    }
}