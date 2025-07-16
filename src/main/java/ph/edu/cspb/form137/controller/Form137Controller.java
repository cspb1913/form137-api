package ph.edu.cspb.form137.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.edu.cspb.form137.model.Form137Request;
import ph.edu.cspb.form137.repository.Form137RequestRepository;

@RestController
@RequestMapping("/api/form137")
public class Form137Controller {

    private final Form137RequestRepository repository;

    public Form137Controller(Form137RequestRepository repository) {
        this.repository = repository;
    }

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> submit(@ModelAttribute Form137Request request) {
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
            return ResponseEntity.status(400).body(body);
        }

        if (request.getTicketNumber() == null) {
            request.setTicketNumber("REQ-" + Instant.now().toString().replaceAll("\\D", "").substring(0, 5));
        }
        request.setSubmittedAt(Instant.now().toString());
        request.setStatus("processing");
        Form137Request saved = repository.save(request);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("ticketNumber", saved.getTicketNumber());
        body.put("message", "Form 137 request submitted successfully");
        body.put("submittedAt", saved.getSubmittedAt());
        return ResponseEntity.status(201).body(body);
    }

    @GetMapping(value = "/status/{ticketNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> status(@PathVariable String ticketNumber) {
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
