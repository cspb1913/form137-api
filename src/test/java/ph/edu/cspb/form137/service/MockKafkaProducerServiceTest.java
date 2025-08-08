package ph.edu.cspb.form137.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ph.edu.cspb.form137.model.Form137Request;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for MockKafkaProducerService.
 * Verifies that the mock service can be used safely in test environments.
 */
@ExtendWith(MockitoExtension.class)
public class MockKafkaProducerServiceTest {
    
    private MockKafkaProducerService mockKafkaProducerService;
    
    @BeforeEach
    public void setUp() {
        mockKafkaProducerService = new MockKafkaProducerService();
    }
    
    @Test
    public void testSendSubmissionNotification_DoesNotThrow() {
        Form137Request request = createTestRequest();
        
        assertDoesNotThrow(() -> {
            mockKafkaProducerService.sendSubmissionNotification(request);
        });
    }
    
    @Test
    public void testSendStatusUpdateNotification_DoesNotThrow() {
        Form137Request request = createTestRequest();
        
        assertDoesNotThrow(() -> {
            mockKafkaProducerService.sendStatusUpdateNotification(request, "processing", "completed");
        });
    }
    
    @Test
    public void testSendNotification_WithNullEmail_DoesNotThrow() {
        Form137Request request = createTestRequest();
        request.setRequesterEmail(null);
        
        assertDoesNotThrow(() -> {
            mockKafkaProducerService.sendSubmissionNotification(request);
            mockKafkaProducerService.sendStatusUpdateNotification(request, "processing", "completed");
        });
    }
    
    @Test
    public void testSendNotification_WithEmptyEmail_DoesNotThrow() {
        Form137Request request = createTestRequest();
        request.setRequesterEmail("");
        
        assertDoesNotThrow(() -> {
            mockKafkaProducerService.sendSubmissionNotification(request);
            mockKafkaProducerService.sendStatusUpdateNotification(request, "processing", "completed");
        });
    }
    
    private Form137Request createTestRequest() {
        Form137Request request = new Form137Request();
        request.setId("test-id-123");
        request.setTicketNumber("REQ-TEST-123");
        request.setLearnerReferenceNumber("123456789012");
        request.setFirstName("Juan");
        request.setMiddleName("Santos");
        request.setLastName("Dela Cruz");
        request.setLearnerName("Juan Dela Cruz");
        request.setRequesterName("Maria Dela Cruz");
        request.setRequesterEmail("test@example.com");
        request.setRelationshipToLearner("Mother");
        request.setMobileNumber("09123456789");
        request.setPreviousSchool("Test School");
        request.setLastGradeLevel("Grade 10");
        request.setLastSchoolYear("2022-2023");
        request.setPurposeOfRequest("Transfer");
        request.setDeliveryMethod("Email");
        request.setRequestType("Original");
        request.setStatus("processing");
        request.setSubmittedAt("2024-01-01T10:00:00Z");
        return request;
    }
}