# Kafka Integration for Form137 API

## Overview
The Form137 API now includes Apache Kafka integration for sending email notifications when forms are submitted or their status is updated. The integration publishes JSON messages to a Kafka topic that is consumed by the f137-k-sendgrid service for email delivery.

## Configuration

### Kafka Connection Details
- **Bootstrap Servers**: `kafka-external.kafka.svc.cluster.local:9092`
- **Topic Name**: `form137-email-notifications`
- **Message Format**: JSON

### Producer Settings
- **Acknowledgments**: `acks=all` (waits for full acknowledgment from all replicas)
- **Compression**: Snappy compression enabled for better throughput
- **Retries**: 3 automatic retries on failure
- **Idempotence**: Enabled to prevent duplicate messages
- **Serialization**: StringSerializer for both keys and values

## Message Structure

Email messages follow this JSON structure:
```json
{
    "to": "recipient@email.com",
    "from": "no-reply@cspb.edu.ph",
    "subject": "Form 137 Request - [Status]",
    "body": "Detailed message about the request"
}
```

## Implementation Details

### Key Components

1. **KafkaConfig** (`config/KafkaConfig.java`)
   - Configures Kafka producer with proper serialization and reliability settings
   - Creates the email notifications topic if it doesn't exist
   - Conditional configuration based on `kafka.enabled` property

2. **KafkaProducerService** (`service/KafkaProducerService.java`)
   - Handles message publishing to Kafka
   - Provides methods for submission and status update notifications
   - Implements async sending with callbacks for better performance
   - Includes comprehensive error handling and logging

3. **MockKafkaProducerService** (`service/MockKafkaProducerService.java`)
   - Mock implementation for testing environments
   - Activated when `kafka.enabled=false`
   - Logs notification actions without requiring Kafka infrastructure

4. **EmailMessage** (`model/EmailMessage.java`)
   - Model representing the email message structure
   - Matches the format expected by the f137-k-sendgrid consumer

### Integration Points

1. **Form Submission** (Form137Controller.java:163-170)
   - Sends notification after successful form submission
   - Includes ticket number, learner details, and request information
   - Non-blocking operation that doesn't fail the request if notification fails

2. **Status Updates** (RequestController.java:180-191)
   - Sends notification when request status changes
   - Includes old and new status in the notification
   - Special handling for completed and ready_for_pickup statuses

## Environment Configuration

### Development (application-dev.yml)
```yaml
kafka:
  enabled: false  # Disabled for local development
```

### Test (application-test.properties)
```properties
kafka.enabled=false  # Disabled for tests
```

### Production (application-prod.yml)
```yaml
kafka:
  enabled: true
  topic:
    email-notifications: form137-email-notifications
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:kafka-external.kafka.svc.cluster.local:9092}
```

## Testing

### Unit Tests
- `MockKafkaProducerServiceTest`: Tests the mock service behavior
- Verifies that the mock service doesn't throw exceptions

### Integration Tests
- `KafkaProducerServiceTest`: Tests with embedded Kafka
- Verifies message publishing and content
- Tests error scenarios (missing email, etc.)

### Running Tests
```bash
# Run all tests
./gradlew test

# Run only Kafka-related tests
./gradlew test --tests "*Kafka*"
```

## Monitoring and Troubleshooting

### Logging
The service includes comprehensive logging at various levels:
- **INFO**: Successful message sends, including topic and offset
- **WARN**: Missing email addresses or non-critical issues
- **ERROR**: Failed message sends or serialization errors

### Key Log Messages
```
INFO: Email notification sent successfully for ticket: REQ-123 to topic: form137-email-notifications with offset: 42
ERROR: Failed to send email notification for ticket: REQ-123
WARN: Cannot send email notification - no email address provided for ticket: REQ-123
```

### Common Issues

1. **Kafka Connection Failures**
   - Check bootstrap servers configuration
   - Verify network connectivity to Kafka cluster
   - Check Kafka broker availability

2. **Serialization Errors**
   - Verify EmailMessage model matches expected format
   - Check for null values in required fields

3. **Topic Not Found**
   - Topic is auto-created with 3 partitions
   - Verify topic creation permissions

## Email Templates

### Submission Notification
- Subject: "Form 137 Request Submitted - Ticket #[TicketNumber]"
- Includes: Learner details, request type, delivery method, current status

### Status Update Notification
- Subject: "Form 137 Request Update - Ticket #[TicketNumber]"
- Includes: Previous status, new status, special instructions for completed requests

## Future Enhancements

1. **Retry Queue**: Implement dead letter queue for failed messages
2. **Template Engine**: Use a template engine for email body generation
3. **Batch Processing**: Support batch email notifications
4. **Analytics**: Add metrics for email delivery success rates
5. **Multi-language Support**: Support email templates in multiple languages