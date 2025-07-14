package ph.edu.cspb.form137.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping(value = "/liveness", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> liveness() {
        return Map.of("status", "UP");
    }

    @GetMapping(value = "/readiness", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> readiness() {
        return Map.of("status", "UP");
    }
}
