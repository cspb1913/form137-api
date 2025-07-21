package ph.edu.cspb.form137.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ph.edu.cspb.form137.model.Comment;
import ph.edu.cspb.form137.model.Form137Request;
import ph.edu.cspb.form137.repository.CommentRepository;
import ph.edu.cspb.form137.repository.Form137RequestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the comments collection separation feature.
 * Tests that comments are properly handled in separate collection while maintaining API compatibility.
 */
class CommentsIntegrationTest {

    @Test
    void testFormSubmissionCreatesComment() {
        // Create mock repositories
        Form137RequestRepository mockRequestRepository = mock(Form137RequestRepository.class);
        CommentRepository mockCommentRepository = mock(CommentRepository.class);
        
        // Create controller
        Form137Controller controller = new Form137Controller(mockRequestRepository, mockCommentRepository);
        
        // Create test request
        Form137Request request = new Form137Request();
        request.setLearnerReferenceNumber("123456789012");
        request.setFirstName("Juan");
        request.setRequesterEmail("juan@example.com");
        
        // Mock repository behavior
        when(mockRequestRepository.save(any(Form137Request.class))).thenAnswer(invocation -> {
            Form137Request req = invocation.getArgument(0);
            req.setId("test-id-123");
            return req;
        });
        
        // Submit the request
        var response = controller.submit(request);
        
        // Verify response
        assertEquals(201, response.getStatusCodeValue());
        
        // Verify that request was saved
        verify(mockRequestRepository, times(1)).save(any(Form137Request.class));
        
        // Verify that a comment was created in the separate collection
        verify(mockCommentRepository, times(1)).save(any(Comment.class));
    }
    
    @Test
    void testDashboardRequestsIncludesSeparateComments() {
        // Create mock repositories
        Form137RequestRepository mockRequestRepository = mock(Form137RequestRepository.class);
        CommentRepository mockCommentRepository = mock(CommentRepository.class);
        
        // Create controller
        DashboardController controller = new DashboardController(mockRequestRepository, mockCommentRepository);
        
        // Create test data
        Form137Request request = new Form137Request();
        request.setId("req-123");
        request.setTicketNumber("REQ-12345");
        request.setStatus("processing");
        request.setLearnerName("Juan Dela Cruz");
        
        Comment comment = new Comment();
        comment.setId("comment-123");
        comment.setRequestId("req-123");
        comment.setMessage("Test comment");
        comment.setType("system");
        
        // Mock repository behavior
        when(mockRequestRepository.findAll()).thenReturn(List.of(request));
        when(mockCommentRepository.findByRequestIdOrderByTimestampAsc("req-123"))
            .thenReturn(List.of(comment));
        
        // Call the endpoint
        var response = controller.listRequests("Bearer token");
        
        // Verify response structure
        assertNotNull(response);
        assertTrue(response.containsKey("requests"));
        assertTrue(response.containsKey("statistics"));
        
        @SuppressWarnings("unchecked")
        List<java.util.Map<String, Object>> requests = 
            (List<java.util.Map<String, Object>>) response.get("requests");
        
        assertEquals(1, requests.size());
        
        java.util.Map<String, Object> requestData = requests.get(0);
        assertEquals("req-123", requestData.get("id"));
        assertEquals("REQ-12345", requestData.get("ticketNumber"));
        
        // Verify comments are included
        assertTrue(requestData.containsKey("comments"));
        @SuppressWarnings("unchecked")
        List<Comment> comments = (List<Comment>) requestData.get("comments");
        assertEquals(1, comments.size());
        assertEquals("Test comment", comments.get(0).getMessage());
        
        // Verify repositories were called correctly
        verify(mockRequestRepository, times(1)).findAll();
        verify(mockCommentRepository, times(1)).findByRequestIdOrderByTimestampAsc("req-123");
    }
    
    @Test
    void testGetRequestCommentsEndpoint() {
        // Create mock repositories
        Form137RequestRepository mockRequestRepository = mock(Form137RequestRepository.class);
        CommentRepository mockCommentRepository = mock(CommentRepository.class);
        
        // Create controller
        DashboardController controller = new DashboardController(mockRequestRepository, mockCommentRepository);
        
        // Create test data
        Form137Request request = new Form137Request();
        request.setId("req-123");
        
        Comment comment1 = new Comment();
        comment1.setId("comment-1");
        comment1.setMessage("First comment");
        
        Comment comment2 = new Comment();
        comment2.setId("comment-2");
        comment2.setMessage("Second comment");
        
        // Mock repository behavior
        when(mockRequestRepository.findById("req-123")).thenReturn(Optional.of(request));
        when(mockCommentRepository.findByRequestIdOrderByTimestampAsc("req-123"))
            .thenReturn(List.of(comment1, comment2));
        
        // Call the endpoint
        var response = controller.getRequestComments("Bearer token", "req-123");
        
        // Verify response
        assertEquals(200, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> responseBody = 
            (java.util.Map<String, Object>) response.getBody();
        
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("comments"));
        
        @SuppressWarnings("unchecked")
        List<Comment> comments = (List<Comment>) responseBody.get("comments");
        assertEquals(2, comments.size());
        assertEquals("First comment", comments.get(0).getMessage());
        assertEquals("Second comment", comments.get(1).getMessage());
        
        // Verify repositories were called correctly
        verify(mockRequestRepository, times(1)).findById("req-123");
        verify(mockCommentRepository, times(1)).findByRequestIdOrderByTimestampAsc("req-123");
    }
    
    @Test
    void testAddCommentToSeparateCollection() {
        // Create mock repositories
        Form137RequestRepository mockRequestRepository = mock(Form137RequestRepository.class);
        CommentRepository mockCommentRepository = mock(CommentRepository.class);
        
        // Create controller
        DashboardController controller = new DashboardController(mockRequestRepository, mockCommentRepository);
        
        // Create test data
        Form137Request request = new Form137Request();
        request.setId("req-123");
        request.setRequesterName("Maria Dela Cruz");
        
        // Mock repository behavior
        when(mockRequestRepository.findById("req-123")).thenReturn(Optional.of(request));
        
        // Prepare input
        java.util.Map<String, String> input = java.util.Map.of("message", "Test comment message");
        
        // Call the endpoint
        var response = controller.addComment("Bearer token", "req-123", input);
        
        // Verify response
        assertEquals(201, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> responseBody = 
            (java.util.Map<String, Object>) response.getBody();
        
        assertNotNull(responseBody);
        assertEquals("Test comment message", responseBody.get("message"));
        assertEquals("user-response", responseBody.get("type"));
        assertEquals("Maria Dela Cruz", responseBody.get("author"));
        
        // Verify that comment was saved to separate collection
        verify(mockCommentRepository, times(1)).save(any(Comment.class));
        
        // Verify that request was NOT updated (we're not storing comments in request anymore)
        verify(mockRequestRepository, never()).save(any(Form137Request.class));
    }
}