package ph.edu.cspb.form137.model;

public class Comment {
    private String id;
    private String message;
    private String registrarName;
    private boolean requiresResponse;
    private String timestamp;
    private String type;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
