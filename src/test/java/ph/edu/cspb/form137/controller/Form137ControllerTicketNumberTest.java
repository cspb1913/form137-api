package ph.edu.cspb.form137.controller;

import org.junit.jupiter.api.Test;
import ph.edu.cspb.form137.model.Form137Request;
import ph.edu.cspb.form137.repository.Form137RequestRepository;
import ph.edu.cspb.form137.repository.CommentRepository;
import ph.edu.cspb.form137.util.TicketNumberGenerator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class Form137ControllerTicketNumberTest {

    @Test
    void testTicketNumberGenerationInController() {
        // Create a mock repository
        Form137RequestRepository mockRepository = mock(Form137RequestRepository.class);
        CommentRepository mockCommentRepository = mock(CommentRepository.class);
        
        // Create controller with mock repositories
        Form137Controller controller = new Form137Controller(mockRepository, mockCommentRepository);
        
        // Create test request with minimal required fields
        Form137Request request = new Form137Request();
        request.setLearnerReferenceNumber("123456789012");
        request.setFirstName("Juan");
        request.setRequesterEmail("juan@example.com");
        
        // Mock the repository save to return the same request with an ID
        when(mockRepository.save(any(Form137Request.class))).thenAnswer(invocation -> {
            Form137Request req = invocation.getArgument(0);
            req.setId("test-id-123"); // Set an ID for the saved request
            return req;
        });
        
        // Call the submit method
        var response = controller.submit(request);
        
        // Verify the response
        assertEquals(201, response.getStatusCodeValue());
        
        // Verify that the request now has a ticket number
        assertNotNull(request.getTicketNumber());
        assertTrue(request.getTicketNumber().startsWith("REQ-"));
        assertEquals(17, request.getTicketNumber().length());
        assertTrue(request.getTicketNumber().matches("REQ-\\d{13}"));
        
        System.out.println("Generated ticket number: " + request.getTicketNumber());
        
        // Verify the response body contains the ticket number
        @SuppressWarnings("unchecked")
        var responseBody = (java.util.Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(request.getTicketNumber(), responseBody.get("ticketNumber"));
        assertEquals("Form 137 request submitted successfully", responseBody.get("message"));
        
        // Verify that a comment was created (should be called once)
        verify(mockCommentRepository, times(1)).save(any());
    }
    
    @Test
    void testTicketNumberUniqueness() {
        // Test that multiple calls generate unique ticket numbers
        String[] ticketNumbers = new String[5];
        
        for (int i = 0; i < 5; i++) {
            ticketNumbers[i] = TicketNumberGenerator.generateTicketNumber();
            System.out.println("Generated ticket number " + (i + 1) + ": " + ticketNumbers[i]);
            
            // Small delay to ensure different timestamps
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Verify all ticket numbers are unique
        for (int i = 0; i < ticketNumbers.length; i++) {
            for (int j = i + 1; j < ticketNumbers.length; j++) {
                assertNotEquals(ticketNumbers[i], ticketNumbers[j], 
                    "Ticket numbers should be unique: " + ticketNumbers[i] + " vs " + ticketNumbers[j]);
            }
        }
        
        // Verify all follow the correct format
        for (String ticketNumber : ticketNumbers) {
            assertTrue(ticketNumber.startsWith("REQ-"));
            assertEquals(17, ticketNumber.length());
            assertTrue(ticketNumber.matches("REQ-\\d{13}"));
        }
    }
}