package ph.edu.cspb.form137.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ph.edu.cspb.form137.model.EmailMessage;
import ph.edu.cspb.form137.model.Form137Request;

import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing email notifications to Kafka.
 * Handles the conversion of Form137 requests to email messages and publishes them
 * to the Kafka topic for processing by the email service.
 */
@Service
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String FROM_EMAIL = "no-reply@cspb.edu.ph";
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${kafka.topic.email-notifications}")
    private String emailTopic;
    
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Sends an email notification for a new Form 137 submission.
     * 
     * @param request The Form137Request that was submitted
     */
    public void sendSubmissionNotification(Form137Request request) {
        if (request.getRequesterEmail() == null || request.getRequesterEmail().isBlank()) {
            logger.warn("Cannot send email notification - no email address provided for ticket: {}", 
                request.getTicketNumber());
            return;
        }
        
        String subject = String.format("Form 137 Request Submitted - Ticket #%s", request.getTicketNumber());
        String body = buildSubmissionEmailBody(request);
        
        EmailMessage emailMessage = new EmailMessage(
            request.getRequesterEmail(),
            FROM_EMAIL,
            subject,
            body
        );
        
        sendEmailMessage(emailMessage, request.getTicketNumber());
    }
    
    /**
     * Sends an email notification for a status update.
     * 
     * @param request The Form137Request with updated status
     * @param oldStatus The previous status
     * @param newStatus The new status
     */
    public void sendStatusUpdateNotification(Form137Request request, String oldStatus, String newStatus) {
        if (request.getRequesterEmail() == null || request.getRequesterEmail().isBlank()) {
            logger.warn("Cannot send email notification - no email address provided for ticket: {}", 
                request.getTicketNumber());
            return;
        }
        
        String subject = String.format("Form 137 Request Update - Ticket #%s", request.getTicketNumber());
        String body = buildStatusUpdateEmailBody(request, oldStatus, newStatus);
        
        EmailMessage emailMessage = new EmailMessage(
            request.getRequesterEmail(),
            FROM_EMAIL,
            subject,
            body
        );
        
        sendEmailMessage(emailMessage, request.getTicketNumber());
    }
    
    /**
     * Sends the email message to Kafka asynchronously.
     * 
     * @param emailMessage The email message to send
     * @param ticketNumber The ticket number for logging purposes
     */
    private void sendEmailMessage(EmailMessage emailMessage, String ticketNumber) {
        try {
            String messageJson = objectMapper.writeValueAsString(emailMessage);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(emailTopic, ticketNumber, messageJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Email notification sent successfully for ticket: {} to topic: {} with offset: {}", 
                        ticketNumber, 
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send email notification for ticket: {}", ticketNumber, ex);
                }
            });
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize email message for ticket: {}", ticketNumber, e);
        }
    }
    
    /**
     * Builds the email body for a new submission.
     */
    private String buildSubmissionEmailBody(Form137Request request) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(request.getRequesterName()).append(",\n\n");
        body.append("Your Form 137 request has been successfully submitted.\n\n");
        body.append("Request Details:\n");
        body.append("----------------\n");
        body.append("Ticket Number: ").append(request.getTicketNumber()).append("\n");
        body.append("Learner Name: ").append(request.getLearnerName()).append("\n");
        body.append("LRN: ").append(request.getLearnerReferenceNumber()).append("\n");
        body.append("Previous School: ").append(request.getPreviousSchool()).append("\n");
        body.append("Last Grade Level: ").append(request.getLastGradeLevel()).append("\n");
        body.append("Last School Year: ").append(request.getLastSchoolYear()).append("\n");
        body.append("Purpose: ").append(request.getPurposeOfRequest()).append("\n");
        body.append("Delivery Method: ").append(request.getDeliveryMethod()).append("\n");
        body.append("Request Type: ").append(request.getRequestType()).append("\n");
        body.append("\n");
        body.append("Current Status: Processing\n\n");
        body.append("You can track your request status using your ticket number.\n");
        body.append("We will notify you via email once your request is ready.\n\n");
        body.append("If you have any questions, please contact the Registrar's Office.\n\n");
        body.append("Thank you,\n");
        body.append("CSPB Registrar's Office");
        
        return body.toString();
    }
    
    /**
     * Builds the email body for a status update.
     */
    private String buildStatusUpdateEmailBody(Form137Request request, String oldStatus, String newStatus) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(request.getRequesterName()).append(",\n\n");
        body.append("Your Form 137 request status has been updated.\n\n");
        body.append("Ticket Number: ").append(request.getTicketNumber()).append("\n");
        body.append("Learner Name: ").append(request.getLearnerName()).append("\n");
        body.append("Previous Status: ").append(formatStatus(oldStatus)).append("\n");
        body.append("New Status: ").append(formatStatus(newStatus)).append("\n\n");
        
        if ("completed".equalsIgnoreCase(newStatus)) {
            body.append("Your Form 137 is now ready!\n");
            body.append("Please proceed to the Registrar's Office to claim your document.\n");
            body.append("Don't forget to bring a valid ID for verification.\n\n");
        } else if ("ready_for_pickup".equalsIgnoreCase(newStatus)) {
            body.append("Your Form 137 is ready for pickup!\n");
            body.append("Please visit the Registrar's Office at your earliest convenience.\n\n");
        } else if ("requires_action".equalsIgnoreCase(newStatus)) {
            body.append("Your request requires additional information or action.\n");
            body.append("Please check the notes or contact the Registrar's Office.\n\n");
            if (request.getNotes() != null && !request.getNotes().isBlank()) {
                body.append("Notes: ").append(request.getNotes()).append("\n\n");
            }
        }
        
        body.append("If you have any questions, please contact the Registrar's Office.\n\n");
        body.append("Thank you,\n");
        body.append("CSPB Registrar's Office");
        
        return body.toString();
    }
    
    /**
     * Formats the status for display in emails.
     */
    private String formatStatus(String status) {
        if (status == null) return "Unknown";
        
        return switch (status.toLowerCase()) {
            case "processing" -> "Processing";
            case "completed" -> "Completed";
            case "ready_for_pickup" -> "Ready for Pickup";
            case "requires_action" -> "Requires Action";
            case "cancelled" -> "Cancelled";
            default -> status;
        };
    }
}