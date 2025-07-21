package ph.edu.cspb.form137.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB entity representing a comment for a Form 137 request.
 * Comments are stored in a separate collection (form137-comments) and linked
 * to Form137Request documents via the requestId field.
 */
@Document(collection = "form137-comments")
public class Comment {
    @Id
    private String id;
    private String requestId; // Links to Form137Request.id
    private String message;
    private String registrarName;
    private boolean requiresResponse;
    private String timestamp;
    private String type;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRegistrarName() { return registrarName; }
    public void setRegistrarName(String registrarName) { this.registrarName = registrarName; }

    public boolean isRequiresResponse() { return requiresResponse; }
    public void setRequiresResponse(boolean requiresResponse) { this.requiresResponse = requiresResponse; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
