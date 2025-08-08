package ph.edu.cspb.form137.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.edu.cspb.form137.model.Comment;
import ph.edu.cspb.form137.model.Form137Request;
import ph.edu.cspb.form137.model.Form137RequestStatus;
import ph.edu.cspb.form137.repository.Form137RequestRepository;
import ph.edu.cspb.form137.repository.CommentRepository;
import ph.edu.cspb.form137.service.KafkaProducerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Request Management", description = "Administrative operations for managing Form 137 request status")
@SecurityRequirement(name = "bearerAuth")
public class RequestController {

    private final Form137RequestRepository repository;
    private final CommentRepository commentRepository;
    private final KafkaProducerService kafkaProducerService;
    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public RequestController(Form137RequestRepository repository, 
                           CommentRepository commentRepository,
                           KafkaProducerService kafkaProducerService) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Operation(
        summary = "Update Request Status", 
        description = "Update the status of a specific Form 137 request (admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Status updated successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "id": "507f1f77bcf86cd799439011",
                        "status": "completed",
                        "updatedAt": "2024-01-01T14:00:00Z",
                        "message": "Status updated successfully"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Request not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "error": "Request not found"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Invalid status value",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "error": "Status update failed.",
                        "message": "Invalid status. Allowed values: pending, processing, completed, rejected, cancelled"
                    }
                    """)
            )
        )
    })
    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRequestStatus(
            @Parameter(description = "Authorization header with Bearer token", required = true, example = "Bearer your-jwt-token")
            @RequestHeader("Authorization") String auth, 
            @Parameter(description = "Request ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id, 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Status update request",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(value = """
                        {
                            "status": "completed"
                        }
                        """)
                )
            )
            @RequestBody Map<String, String> input) {
        
        logger.info("updateRequestStatus called with Authorization: {}, id: {}, input: {}", auth, id, input);
        
        // Validate request exists
        Optional<Form137Request> requestOpt = repository.findById(id);
        if (requestOpt.isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Request not found");
            try {
                logger.info("updateRequestStatus response (not found): {}", objectMapper.writeValueAsString(body));
            } catch (Exception e) {
                logger.warn("Failed to serialize updateRequestStatus not found response", e);
            }
            return ResponseEntity.status(404).body(body);
        }
        
        // Validate status value using enum
        String newStatus = input.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Status update failed.");
            body.put("message", "Status field is required");
            return ResponseEntity.status(422).body(body);
        }
        
        Form137RequestStatus statusEnum;
        try {
            statusEnum = Form137RequestStatus.fromValue(newStatus);
            newStatus = statusEnum.getValue(); // Use the normalized lowercase value
        } catch (IllegalArgumentException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Status update failed.");
            body.put("message", e.getMessage());
            try {
                logger.info("updateRequestStatus response (invalid status): {}", objectMapper.writeValueAsString(body));
            } catch (Exception ex) {
                logger.warn("Failed to serialize updateRequestStatus invalid status response", ex);
            }
            return ResponseEntity.status(422).body(body);
        }
        
        // Update the request
        Form137Request request = requestOpt.get();
        String oldStatus = request.getStatus();
        request.setStatus(newStatus);
        request.setUpdatedAt(java.time.Instant.now().toString());
        
        try {
            repository.save(request);
            
            // Create audit log entry as a comment
            Comment auditComment = new Comment();
            auditComment.setId("audit_" + System.currentTimeMillis());
            auditComment.setRequestId(request.getId());
            auditComment.setMessage(String.format("Status updated from %s to %s by admin", 
                oldStatus != null ? oldStatus : "UNKNOWN", newStatus));
            auditComment.setTimestamp(java.time.Instant.now().toString());
            auditComment.setType("system");
            auditComment.setRegistrarName("System");
            auditComment.setRequiresResponse(false);
            commentRepository.save(auditComment);
            
            // Send email notification via Kafka for status update
            try {
                kafkaProducerService.sendStatusUpdateNotification(request, oldStatus, newStatus);
                logger.info("Email notification sent for status update - Ticket: {}, Status: {} -> {}", 
                    request.getTicketNumber(), oldStatus, newStatus);
            } catch (Exception e) {
                logger.error("Failed to send email notification for status update - Ticket: {}", 
                    request.getTicketNumber(), e);
                // Don't fail the status update if email notification fails
            }
            
            // Success response
            Map<String, Object> body = new HashMap<>();
            body.put("id", request.getId());
            body.put("status", request.getStatus());
            body.put("updatedAt", request.getUpdatedAt());
            body.put("message", "Status updated successfully");
            
            try {
                logger.info("updateRequestStatus response (success): {}", objectMapper.writeValueAsString(body));
            } catch (Exception e) {
                logger.warn("Failed to serialize updateRequestStatus success response", e);
            }
            
            return ResponseEntity.ok(body);
            
        } catch (Exception e) {
            logger.error("Failed to update request status", e);
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Status update failed.");
            body.put("message", "Internal server error occurred while updating status");
            return ResponseEntity.status(500).body(body);
        }
    }
}