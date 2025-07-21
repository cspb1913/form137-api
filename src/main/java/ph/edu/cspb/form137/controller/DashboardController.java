package ph.edu.cspb.form137.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.edu.cspb.form137.model.Comment;
import ph.edu.cspb.form137.model.Form137Request;
import ph.edu.cspb.form137.repository.Form137RequestRepository;
import ph.edu.cspb.form137.repository.CommentRepository;

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
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Administrative dashboard operations for managing Form 137 requests")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final Form137RequestRepository repository;
    private final CommentRepository commentRepository;
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public DashboardController(Form137RequestRepository repository, CommentRepository commentRepository) {
        this.repository = repository;
        this.commentRepository = commentRepository;
    }

    @Operation(
        summary = "List All Requests", 
        description = "Get a list of all Form 137 requests with statistics"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Requests retrieved successfully",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                    "requests": [
                        {
                            "id": "507f1f77bcf86cd799439011",
                            "ticketNumber": "REQ-12345",
                            "learnerName": "Juan Dela Cruz",
                            "learnerReferenceNumber": "123456789012",
                            "status": "processing",
                            "submittedDate": "2024-01-01T10:00:00Z",
                            "requesterName": "Maria Dela Cruz",
                            "requesterEmail": "maria@email.com",
                            "requestType": "Original",
                            "deliveryMethod": "Email"
                        }
                    ],
                    "statistics": {
                        "totalRequests": 5,
                        "pendingRequests": 2,
                        "completedRequests": 3,
                        "averageProcessingTime": 7
                    }
                }
                """)
        )
    )
    @GetMapping(value = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> listRequests(
            @Parameter(description = "Authorization header with Bearer token", required = true, example = "Bearer your-jwt-token")
            @RequestHeader("Authorization") String auth) {
        logger.info("listRequests called with Authorization: {}", auth);
        List<Form137Request> all = repository.findAll();
        List<Map<String, Object>> requests = new java.util.ArrayList<>();
        for (Form137Request r : all) {
            Map<String, Object> map = new HashMap<>();
            // Fetch comments from separate collection
            List<Comment> comments = commentRepository.findByRequestIdOrderByTimestampAsc(r.getId());
            map.put("comments", comments);
            map.put("deliveryMethod", r.getDeliveryMethod());
            map.put("estimatedCompletion", r.getEstimatedCompletion());
            map.put("id", r.getId());
            map.put("learnerName", r.getLearnerName());
            map.put("learnerReferenceNumber", r.getLearnerReferenceNumber());
            map.put("requestType", r.getRequestType());
            map.put("requesterEmail", r.getRequesterEmail());
            map.put("requesterName", r.getRequesterName());
            map.put("status", r.getStatus());
            map.put("submittedDate", r.getSubmittedAt());
            map.put("ticketNumber", r.getTicketNumber());
            requests.add(map);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("averageProcessingTime", 7);
        stats.put("completedRequests", 3);
        stats.put("pendingRequests", 2);
        stats.put("totalRequests", requests.size());

        Map<String, Object> body = new HashMap<>();
        body.put("requests", requests);
        body.put("statistics", stats);
        try {
            logger.info("listRequests response: {}", objectMapper.writeValueAsString(body));
        } catch (Exception e) {
            logger.warn("Failed to serialize listRequests response", e);
        }
        return body;
    }

    @Operation(
        summary = "Get Request Details", 
        description = "Get detailed information about a specific Form 137 request"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Request details retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "id": "507f1f77bcf86cd799439011",
                        "ticketNumber": "REQ-12345",
                        "learnerName": "Juan Dela Cruz",
                        "learnerReferenceNumber": "123456789012",
                        "status": "processing",
                        "submittedDate": "2024-01-01T10:00:00Z",
                        "requesterName": "Maria Dela Cruz",
                        "requesterEmail": "maria@email.com",
                        "requestType": "Original",
                        "deliveryMethod": "Email",
                        "comments": []
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
                        "code": "REQUEST_NOT_FOUND",
                        "error": "Request not found"
                    }
                    """)
            )
        )
    })
    @GetMapping(value = "/request/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> requestDetails(
            @Parameter(description = "Authorization header with Bearer token", required = true, example = "Bearer your-jwt-token")
            @RequestHeader("Authorization") String auth, 
            @Parameter(description = "Request ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        logger.info("requestDetails called with Authorization: {}, id: {}", auth, id);
        Optional<Form137Request> req = repository.findById(id);
        if (req.isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("code", "REQUEST_NOT_FOUND");
            body.put("error", "Request not found");
            try {
                logger.info("requestDetails response (not found): {}", objectMapper.writeValueAsString(body));
            } catch (Exception e) {
                logger.warn("Failed to serialize requestDetails not found response", e);
            }
            return ResponseEntity.status(404).body(body);
        }
        Form137Request r = req.get();
        Map<String, Object> body = new HashMap<>();
        // Fetch comments from separate collection
        List<Comment> comments = commentRepository.findByRequestIdOrderByTimestampAsc(r.getId());
        body.put("comments", comments);
        body.put("deliveryMethod", r.getDeliveryMethod());
        body.put("estimatedCompletion", r.getEstimatedCompletion());
        body.put("id", r.getId());
        body.put("learnerName", r.getLearnerName());
        body.put("learnerReferenceNumber", r.getLearnerReferenceNumber());
        body.put("requestType", r.getRequestType());
        body.put("requesterEmail", r.getRequesterEmail());
        body.put("requesterName", r.getRequesterName());
        body.put("status", r.getStatus());
        body.put("submittedDate", r.getSubmittedAt());
        body.put("ticketNumber", r.getTicketNumber());
        try {
            logger.info("requestDetails response: {}", objectMapper.writeValueAsString(body));
        } catch (Exception e) {
            logger.warn("Failed to serialize requestDetails response", e);
        }
        return ResponseEntity.ok(body);
    }

    @Operation(
        summary = "Add Comment to Request", 
        description = "Add a comment or note to a specific Form 137 request"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Comment added successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "id": "c1640995200000",
                        "message": "Additional documentation required",
                        "timestamp": "2024-01-01T12:00:00Z",
                        "type": "user-response",
                        "author": "Maria Dela Cruz"
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
                        "code": "REQUEST_NOT_FOUND",
                        "error": "Request not found"
                    }
                    """)
            )
        )
    })
    @PostMapping(value = "/request/{id}/comment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addComment(
            @Parameter(description = "Authorization header with Bearer token", required = true, example = "Bearer your-jwt-token")
            @RequestHeader("Authorization") String auth, 
            @Parameter(description = "Request ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id, 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Comment to add",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(value = """
                        {
                            "message": "Additional documentation required"
                        }
                        """)
                )
            )
            @RequestBody Map<String, String> input) {
        Optional<Form137Request> req = repository.findById(id);
        if (req.isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("code", "REQUEST_NOT_FOUND");
            body.put("error", "Request not found");
            return ResponseEntity.status(404).body(body);
        }
        Form137Request r = req.get();
        
        // Create new comment in separate collection
        Comment c = new Comment();
        c.setId("c" + System.currentTimeMillis());
        c.setRequestId(r.getId()); // Link to the request
        c.setMessage(input.get("message"));
        c.setTimestamp(java.time.Instant.now().toString());
        c.setType("user-response");
        
        // Save comment to the separate collection
        commentRepository.save(c);
        
        Map<String, Object> body = new HashMap<>();
        body.put("author", r.getRequesterName());
        body.put("id", c.getId());
        body.put("message", c.getMessage());
        body.put("timestamp", c.getTimestamp());
        body.put("type", c.getType());
        return ResponseEntity.status(201).body(body);
    }

    @Operation(
        summary = "Get Request Comments", 
        description = "Get all comments for a specific Form 137 request"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Comments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "comments": [
                            {
                                "id": "comment_001",
                                "message": "Your request has been received",
                                "registrarName": "Ms. Santos",
                                "requiresResponse": false,
                                "timestamp": "2024-01-15T10:35:00Z",
                                "type": "info"
                            }
                        ]
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
                        "code": "REQUEST_NOT_FOUND",
                        "error": "Request not found"
                    }
                    """)
            )
        )
    })
    @GetMapping(value = "/request/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRequestComments(
            @Parameter(description = "Authorization header with Bearer token", required = true, example = "Bearer your-jwt-token")
            @RequestHeader("Authorization") String auth, 
            @Parameter(description = "Request ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        logger.info("getRequestComments called with Authorization: {}, id: {}", auth, id);
        
        // Check if request exists
        Optional<Form137Request> req = repository.findById(id);
        if (req.isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("code", "REQUEST_NOT_FOUND");
            body.put("error", "Request not found");
            try {
                logger.info("getRequestComments response (not found): {}", objectMapper.writeValueAsString(body));
            } catch (Exception e) {
                logger.warn("Failed to serialize getRequestComments not found response", e);
            }
            return ResponseEntity.status(404).body(body);
        }
        
        // Fetch comments from separate collection
        List<Comment> comments = commentRepository.findByRequestIdOrderByTimestampAsc(id);
        
        Map<String, Object> body = new HashMap<>();
        body.put("comments", comments);
        try {
            logger.info("getRequestComments response: {}", objectMapper.writeValueAsString(body));
        } catch (Exception e) {
            logger.warn("Failed to serialize getRequestComments response", e);
        }
        return ResponseEntity.ok(body);
    }
}
