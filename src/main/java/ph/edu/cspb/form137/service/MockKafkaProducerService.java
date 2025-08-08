package ph.edu.cspb.form137.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ph.edu.cspb.form137.model.Form137Request;

/**
 * Mock implementation of KafkaProducerService for testing environments.
 * This service is activated when kafka.enabled=false, allowing the application
 * to run without a Kafka broker for development and testing.
 */
@Service
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "false")
public class MockKafkaProducerService extends KafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockKafkaProducerService.class);
    
    public MockKafkaProducerService() {
        super(null);
    }
    
    @Override
    public void sendSubmissionNotification(Form137Request request) {
        logger.info("MOCK: Email notification would be sent for new submission - Ticket: {}, Email: {}", 
            request.getTicketNumber(), 
            request.getRequesterEmail());
        
        logger.debug("MOCK: Email Details - To: {}, Subject: Form 137 Request Submitted - Ticket #{}", 
            request.getRequesterEmail(), 
            request.getTicketNumber());
    }
    
    @Override
    public void sendStatusUpdateNotification(Form137Request request, String oldStatus, String newStatus) {
        logger.info("MOCK: Email notification would be sent for status update - Ticket: {}, Email: {}, Status: {} -> {}", 
            request.getTicketNumber(), 
            request.getRequesterEmail(),
            oldStatus,
            newStatus);
        
        logger.debug("MOCK: Email Details - To: {}, Subject: Form 137 Request Update - Ticket #{}", 
            request.getRequesterEmail(), 
            request.getTicketNumber());
    }
}