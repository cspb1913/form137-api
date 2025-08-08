package ph.edu.cspb.form137.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Email message model matching the format expected by f137-k-sendgrid consumer.
 * This model represents the JSON payload structure for email notifications
 * sent via Kafka to the email service.
 */
public class EmailMessage {
    
    @JsonProperty("to")
    private String to;
    
    @JsonProperty("from")
    private String from;
    
    @JsonProperty("subject")
    private String subject;
    
    @JsonProperty("body")
    private String body;
    
    public EmailMessage() {
    }
    
    public EmailMessage(String to, String from, String subject, String body) {
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.body = body;
    }
    
    // Getters and setters
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    @Override
    public String toString() {
        return "EmailMessage{" +
                "to='" + to + '\'' +
                ", from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}