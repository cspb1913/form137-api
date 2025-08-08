package ph.edu.cspb.form137.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ph.edu.cspb.form137.model.EmailMessage;
import ph.edu.cspb.form137.model.Form137Request;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for KafkaProducerService using embedded Kafka.
 */
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"form137-email-notifications"})
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "kafka.enabled=true",
    "kafka.topic.email-notifications=form137-email-notifications"
})
public class KafkaProducerServiceTest {
    
    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    public void testSendSubmissionNotification() throws Exception {
        // Setup test consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        DefaultKafkaConsumerFactory<String, String> consumerFactory = 
            new DefaultKafkaConsumerFactory<>(consumerProps);
        
        ContainerProperties containerProperties = new ContainerProperties("form137-email-notifications");
        KafkaMessageListenerContainer<String, String> container = 
            new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        
        BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) records::add);
        container.start();
        
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        
        // Create test request
        Form137Request request = createTestRequest();
        
        // Send notification
        kafkaProducerService.sendSubmissionNotification(request);
        
        // Verify message received
        ConsumerRecord<String, String> received = records.poll(10, TimeUnit.SECONDS);
        assertNotNull(received, "Should receive a message");
        assertEquals("REQ-TEST-123", received.key());
        
        // Verify message content
        EmailMessage emailMessage = objectMapper.readValue(received.value(), EmailMessage.class);
        assertEquals("test@example.com", emailMessage.getTo());
        assertEquals("no-reply@cspb.edu.ph", emailMessage.getFrom());
        assertTrue(emailMessage.getSubject().contains("REQ-TEST-123"));
        assertTrue(emailMessage.getBody().contains("Juan Dela Cruz"));
        
        container.stop();
    }
    
    @Test
    public void testSendStatusUpdateNotification() throws Exception {
        // Setup test consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group-2", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        DefaultKafkaConsumerFactory<String, String> consumerFactory = 
            new DefaultKafkaConsumerFactory<>(consumerProps);
        
        ContainerProperties containerProperties = new ContainerProperties("form137-email-notifications");
        KafkaMessageListenerContainer<String, String> container = 
            new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        
        BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) records::add);
        container.start();
        
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        
        // Create test request
        Form137Request request = createTestRequest();
        
        // Send status update notification
        kafkaProducerService.sendStatusUpdateNotification(request, "processing", "completed");
        
        // Verify message received
        ConsumerRecord<String, String> received = records.poll(10, TimeUnit.SECONDS);
        assertNotNull(received, "Should receive a message");
        assertEquals("REQ-TEST-123", received.key());
        
        // Verify message content
        EmailMessage emailMessage = objectMapper.readValue(received.value(), EmailMessage.class);
        assertEquals("test@example.com", emailMessage.getTo());
        assertTrue(emailMessage.getSubject().contains("Update"));
        assertTrue(emailMessage.getBody().contains("completed"));
        assertTrue(emailMessage.getBody().contains("ready"));
        
        container.stop();
    }
    
    @Test
    public void testNoEmailNotificationWhenEmailMissing() throws Exception {
        // Setup test consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group-3", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        DefaultKafkaConsumerFactory<String, String> consumerFactory = 
            new DefaultKafkaConsumerFactory<>(consumerProps);
        
        ContainerProperties containerProperties = new ContainerProperties("form137-email-notifications");
        KafkaMessageListenerContainer<String, String> container = 
            new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        
        BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) records::add);
        container.start();
        
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        
        // Create test request without email
        Form137Request request = createTestRequest();
        request.setRequesterEmail(null);
        
        // Try to send notification
        kafkaProducerService.sendSubmissionNotification(request);
        
        // Verify no message received
        ConsumerRecord<String, String> received = records.poll(2, TimeUnit.SECONDS);
        assertNull(received, "Should not receive a message when email is missing");
        
        container.stop();
    }
    
    private Form137Request createTestRequest() {
        Form137Request request = new Form137Request();
        request.setId("test-id-123");
        request.setTicketNumber("REQ-TEST-123");
        request.setLearnerReferenceNumber("123456789012");
        request.setFirstName("Juan");
        request.setMiddleName("Santos");
        request.setLastName("Dela Cruz");
        request.setLearnerName("Juan Dela Cruz");
        request.setRequesterName("Maria Dela Cruz");
        request.setRequesterEmail("test@example.com");
        request.setRelationshipToLearner("Mother");
        request.setMobileNumber("09123456789");
        request.setPreviousSchool("Test School");
        request.setLastGradeLevel("Grade 10");
        request.setLastSchoolYear("2022-2023");
        request.setPurposeOfRequest("Transfer");
        request.setDeliveryMethod("Email");
        request.setRequestType("Original");
        request.setStatus("processing");
        request.setSubmittedAt("2024-01-01T10:00:00Z");
        return request;
    }
}