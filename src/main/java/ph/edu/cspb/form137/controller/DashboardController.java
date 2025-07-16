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

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final Form137RequestRepository repository;

    public DashboardController(Form137RequestRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> listRequests(@RequestHeader("Authorization") String auth) {
        List<Form137Request> all = repository.findAll();
        List<Map<String, Object>> requests = new java.util.ArrayList<>();
        for (Form137Request r : all) {
            Map<String, Object> map = new HashMap<>();
            map.put("comments", r.getComments());
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
        return body;
    }

    @GetMapping(value = "/request/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> requestDetails(@RequestHeader("Authorization") String auth, @PathVariable String id) {
        Optional<Form137Request> req = repository.findById(id);
        if (req.isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("code", "REQUEST_NOT_FOUND");
            body.put("error", "Request not found");
            return ResponseEntity.status(404).body(body);
        }
        Form137Request r = req.get();
        Map<String, Object> body = new HashMap<>();
        body.put("comments", r.getComments());
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
        return ResponseEntity.ok(body);
    }

    @PostMapping(value = "/request/{id}/comment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addComment(@RequestHeader("Authorization") String auth, @PathVariable String id, @RequestBody Map<String, String> input) {
        Optional<Form137Request> req = repository.findById(id);
        if (req.isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("code", "REQUEST_NOT_FOUND");
            body.put("error", "Request not found");
            return ResponseEntity.status(404).body(body);
        }
        Form137Request r = req.get();
        java.util.List<Comment> comments = new java.util.ArrayList<>();
        if (r.getComments() != null) {
            comments.addAll(r.getComments());
        }
        Comment c = new Comment();
        c.setId("c" + System.currentTimeMillis());
        c.setMessage(input.get("message"));
        c.setTimestamp(java.time.Instant.now().toString());
        c.setType("user-response");
        comments.add(c);
        r.setComments(comments);
        repository.save(r);
        Map<String, Object> body = new HashMap<>();
        body.put("author", r.getRequesterName());
        body.put("id", c.getId());
        body.put("message", c.getMessage());
        body.put("timestamp", c.getTimestamp());
        body.put("type", c.getType());
        return ResponseEntity.status(201).body(body);
    }
}
