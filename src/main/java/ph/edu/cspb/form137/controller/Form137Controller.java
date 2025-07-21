package ph.edu.cspb.form137.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.edu.cspb.form137.model.Form137Request;
import ph.edu.cspb.form137.repository.Form137RequestRepository;
import ph.edu.cspb.form137.repository.CommentRepository;
import ph.edu.cspb.form137.model.Comment;
import ph.edu.cspb.form137.util.TicketNumberGenerator;

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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/form137")
@Tag(name = "Form 137 Requests", description = "Operations for submitting and tracking Form 137 transcript requests")
public class Form137Controller {

    private final Form137RequestRepository repository;
    private final CommentRepository commentRepository;
    private static final Logger logger = LoggerFactory.getLogger(Form137Controller.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Form137Controller(Form137RequestRepository repository, CommentRepository commentRepository) {
        this.repository = repository;
        this.commentRepository = commentRepository;
    }

    @Operation(
        summary = "Submit Form 137 Request", 
        description = "Submit a new Form 137 transcript request with student and requester information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Request submitted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "ticketNumber": "REQ-12345",
                        "message": "Form 137 request submitted successfully",
                        "submittedAt": "2024-01-01T10:00:00Z"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Validation error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "error": "Validation Error",
                        "message": "Form validation failed",
                        "statusCode": 400,
                        "details": {
                            "learnerReferenceNumber": ["Must be exactly 12 digits"],
                            "firstName": ["First name is required"],
                            "emailAddress": ["Please enter a valid email address"]
                        }
                    }
                    """)
            )
        )
    })
    @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> submit(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Form 137 request details",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = Form137Request.class),
                    examples = @ExampleObject(value = """
                        {
                            "learnerReferenceNumber": "123456789012",
                            "firstName": "Juan",
                            "middleName": "Santos",
                            "lastName": "Dela Cruz",
                            "dateOfBirth": "2000-01-01",
                            "lastGradeLevel": "Grade 12",
                            "lastSchoolYear": "2018-2019",
                            "previousSchool": "CSPB High School",
                            "purposeOfRequest": "College admission",
                            "deliveryMethod": "Email",
                            "requestType": "Original",
                            "learnerName": "Juan Santos Dela Cruz",
                            "requesterName": "Maria Dela Cruz",
                            "relationshipToLearner": "Mother",
                            "emailAddress": "maria@email.com",
                            "mobileNumber": "09123456789"
                        }
                        """)
                )
            )
            @RequestBody Form137Request request) {
        try {
            logger.info("Incoming payload: {}", objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            logger.warn("Failed to serialize incoming payload", e);
        }
        Map<String, Object> errors = new HashMap<>();
        if (request.getLearnerReferenceNumber() == null || !request.getLearnerReferenceNumber().matches("\\d{12}")) {
            errors.put("learnerReferenceNumber", new String[]{"Must be exactly 12 digits"});
        }
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            errors.put("firstName", new String[]{"First name is required"});
        }
        if (request.getRequesterEmail() == null || !request.getRequesterEmail().contains("@")) {
            errors.put("emailAddress", new String[]{"Please enter a valid email address"});
        }
        if (!errors.isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Validation Error");
            body.put("message", "Form validation failed");
            body.put("statusCode", 400);
            body.put("details", errors);
            try {
                logger.info("Outgoing payload: {}", objectMapper.writeValueAsString(body));
            } catch (Exception e) {
                logger.warn("Failed to serialize outgoing payload", e);
            }
            return ResponseEntity.status(400).body(body);
        }

        if (request.getTicketNumber() == null) {
            request.setTicketNumber(TicketNumberGenerator.generateTicketNumber());
        }
        request.setSubmittedAt(Instant.now().toString());
        request.setStatus("processing");
        Form137Request saved = repository.save(request);

        // Create initial comment in the separate comments collection
        Comment initialComment = new Comment();
        initialComment.setId("c" + System.currentTimeMillis());
        initialComment.setRequestId(saved.getId());
        initialComment.setMessage("Form 137 request has been submitted and is being processed");
        initialComment.setTimestamp(Instant.now().toString());
        initialComment.setType("system");
        initialComment.setRegistrarName("System");
        initialComment.setRequiresResponse(false);
        commentRepository.save(initialComment);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("ticketNumber", saved.getTicketNumber());
        body.put("message", "Form 137 request submitted successfully");
        body.put("submittedAt", saved.getSubmittedAt());
        try {
            logger.info("Outgoing payload: {}", objectMapper.writeValueAsString(body));
        } catch (Exception e) {
            logger.warn("Failed to serialize outgoing payload", e);
        }
        return ResponseEntity.status(201).body(body);
    }

    @Operation(
        summary = "Get Request Status", 
        description = "Get the current status of a Form 137 request using the ticket number"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Request status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "ticketNumber": "REQ-12345",
                        "status": "processing",
                        "submittedAt": "2024-01-01T10:00:00Z",
                        "updatedAt": "2024-01-01T11:00:00Z",
                        "notes": "Request is being processed"
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
                        "error": "Not Found",
                        "message": "Submission not found",
                        "statusCode": 404
                    }
                    """)
            )
        )
    })
    @GetMapping(value = "/status/{ticketNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> status(
            @Parameter(description = "Ticket number to check status for", required = true, example = "REQ-12345")
            @PathVariable String ticketNumber) {
        Optional<Form137Request> result = repository.findByTicketNumber(ticketNumber);
        if (result.isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Not Found");
            body.put("message", "Submission not found");
            body.put("statusCode", 404);
            return ResponseEntity.status(404).body(body);
        }
        Form137Request req = result.get();
        Map<String, Object> body = new HashMap<>();
        body.put("ticketNumber", req.getTicketNumber());
        body.put("status", req.getStatus());
        body.put("submittedAt", req.getSubmittedAt());
        body.put("updatedAt", req.getUpdatedAt());
        body.put("notes", req.getNotes());
        return ResponseEntity.ok(body);
    }
}
