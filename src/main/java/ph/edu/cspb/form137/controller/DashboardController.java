package ph.edu.cspb.form137.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final Environment env;

    public DashboardController(Environment env) {
        this.env = env;
    }

    @GetMapping(value = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> listRequests(@RequestHeader("Authorization") String auth) {
        Map<String, Object> comment = new HashMap<>();
        comment.put("id", "comment_001");
        comment.put("message", "Your request has been received");
        comment.put("registrarName", "Ms. Santos");
        comment.put("requiresResponse", false);
        comment.put("timestamp", "2024-01-15T10:35:00Z");
        comment.put("type", "info");

        Map<String, Object> request = new HashMap<>();
        request.put("comments", List.of(comment));
        request.put("deliveryMethod", "pickup");
        request.put("estimatedCompletion", "2024-01-22T17:00:00Z");
        request.put("id", "req_001");
        request.put("learnerName", "Juan Dela Cruz");
        request.put("learnerReferenceNumber", "123456789012");
        request.put("requestType", "Original Copy");
        request.put("requesterEmail", "maria@email.com");
        request.put("requesterName", "Maria Dela Cruz");
        request.put("status", "submitted");
        request.put("submittedDate", "2024-01-15T10:30:00Z");
        request.put("ticketNumber", "F137-2024-001");

        List<Map<String, Object>> requests = new ArrayList<>();
        requests.add(request);

        if (env.acceptsProfiles(Profiles.of("dev"))) {
            Map<String, Object> comment2 = new HashMap<>();
            comment2.put("id", "comment_002");
            comment2.put("message", "Your request is being processed");
            comment2.put("registrarName", "Mr. Reyes");
            comment2.put("requiresResponse", false);
            comment2.put("timestamp", "2024-01-20T11:00:00Z");
            comment2.put("type", "info");

            Map<String, Object> request2 = new HashMap<>();
            request2.put("comments", List.of(comment2));
            request2.put("deliveryMethod", "email");
            request2.put("estimatedCompletion", "2024-01-27T17:00:00Z");
            request2.put("id", "req_002");
            request2.put("learnerName", "Pedro Santos");
            request2.put("learnerReferenceNumber", "987654321098");
            request2.put("requestType", "Certified True Copy");
            request2.put("requesterEmail", "pedro@email.com");
            request2.put("requesterName", "Pedro Santos");
            request2.put("status", "processing");
            request2.put("submittedDate", "2024-01-20T10:00:00Z");
            request2.put("ticketNumber", "F137-2024-002");

            requests.add(request2);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("averageProcessingTime", 7);
        stats.put("completedRequests", 3);
        stats.put("pendingRequests", 2);
        stats.put("totalRequests", requests.size());

        Map<String, Object> body = new HashMap<>();
        body.put("requests", requests);
        body.put("statistics", stats);
        return body;
    }

    @GetMapping(value = "/request/nonexistent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> nonExisting(@RequestHeader("Authorization") String auth) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", "REQUEST_NOT_FOUND");
        body.put("error", "Request not found");
        return ResponseEntity.status(404).body(body);
    }

    @GetMapping(value = "/request/req_001", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> requestDetails(@RequestHeader("Authorization") String auth) {
        Map<String, Object> comment = new HashMap<>();
        comment.put("id", "comment_001");
        comment.put("message", "Your request has been received");
        comment.put("registrarName", "Ms. Santos");
        comment.put("requiresResponse", false);
        comment.put("timestamp", "2024-01-15T10:35:00Z");
        comment.put("type", "info");

        Map<String, Object> body = new HashMap<>();
        body.put("comments", List.of(comment));
        body.put("deliveryMethod", "pickup");
        body.put("estimatedCompletion", "2024-01-22T17:00:00Z");
        body.put("id", "req_001");
        body.put("learnerName", "Juan Dela Cruz");
        body.put("learnerReferenceNumber", "123456789012");
        body.put("requestType", "Original Copy");
        body.put("requesterEmail", "maria@email.com");
        body.put("requesterName", "Maria Dela Cruz");
        body.put("status", "submitted");
        body.put("submittedDate", "2024-01-15T10:30:00Z");
        body.put("ticketNumber", "F137-2024-001");
        return body;
    }

    @PostMapping(value = "/request/req_001/comment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> addComment(@RequestHeader("Authorization") String auth, @RequestBody Map<String, String> input) {
        Map<String, Object> body = new HashMap<>();
        body.put("author", "Maria Dela Cruz");
        body.put("id", "comment_002");
        body.put("message", input.get("message"));
        body.put("timestamp", "2024-01-16T14:30:00Z");
        body.put("type", "user-response");
        return ResponseEntity.status(201).body(body);
    }
}
