package ph.edu.cspb.form137.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.edu.cspb.form137.model.Form137Request;

@RestController
@RequestMapping("/api/form137")
public class Form137Controller {

    private String scenario = "success";

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> submit() {
        if ("invalid".equals(scenario)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Validation Error");
            body.put("message", "Form validation failed");
            body.put("statusCode", 400);
            Map<String, Object> details = new HashMap<>();
            details.put("learnerReferenceNumber", new String[]{"Must be exactly 12 digits"});
            details.put("firstName", new String[]{"First name is required"});
            details.put("emailAddress", new String[]{"Please enter a valid email address"});
            body.put("details", details);
            return ResponseEntity.status(400).body(body);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        String ticket = "self".equals(scenario) ? "REQ-2025-00124" : "REQ-2025-00123";
        body.put("ticketNumber", ticket);
        body.put("message", "Form 137 request submitted successfully");
        body.put("submittedAt", "2025-01-11T21:52:11.000Z");
        return ResponseEntity.status(201).body(body);
    }

    @GetMapping(value = "/status/{ticketNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> status(@PathVariable String ticketNumber) {
        if ("REQ-2025-00123".equals(ticketNumber)) {
            Map<String, Object> body = new HashMap<>();
            body.put("ticketNumber", "REQ-2025-00123");
            body.put("status", "processing");
            body.put("submittedAt", "2025-01-11T21:52:11.000Z");
            body.put("updatedAt", "2025-01-12T09:30:00.000Z");
            body.put("notes", "Documents under review");
            return ResponseEntity.ok(body);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Not Found");
        body.put("message", "Submission not found");
        body.put("statusCode", 404);
        return ResponseEntity.status(404).body(body);
    }
}
