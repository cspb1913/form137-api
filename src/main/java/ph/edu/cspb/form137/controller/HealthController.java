package ph.edu.cspb.form137.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Health monitoring endpoints for application status")
public class HealthController {

    @Operation(
        summary = "Liveness Check", 
        description = "Check if the application is running and alive"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Application is alive",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Map.class),
            examples = @ExampleObject(value = """
                {
                    "status": "UP"
                }
                """)
        )
    )
    @GetMapping(value = "/liveness", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> liveness() {
        return Map.of("status", "UP");
    }

    @Operation(
        summary = "Readiness Check", 
        description = "Check if the application is ready to serve requests"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Application is ready",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Map.class),
            examples = @ExampleObject(value = """
                {
                    "status": "UP"
                }
                """)
        )
    )
    @GetMapping(value = "/readiness", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> readiness() {
        return Map.of("status", "UP");
    }
}
