package ph.edu.cspb.form137.pact;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.*;

import ph.edu.cspb.form137.model.Comment;
import ph.edu.cspb.form137.model.Form137Request;
import ph.edu.cspb.form137.repository.Form137RequestRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude=" +
                        "de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
                "auth.enabled=false",
                "spring.security.oauth2.resourceserver.jwt.issuer-uri="
        })
@Provider("DashboardAPI")
@PactFolder("pacts")
class DashboardApiProviderPactTest {

    @LocalServerPort
    private int port;

    @MockBean
    Form137RequestRepository repository;

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("user has form 137 requests")
    public void userHasRequests() {
        Comment comment = new Comment();
        comment.setId("comment_001");
        comment.setMessage("Your request has been received");
        comment.setRegistrarName("Ms. Santos");
        comment.setRequiresResponse(false);
        comment.setTimestamp("2024-01-15T10:35:00Z");
        comment.setType("info");

        Form137Request req = new Form137Request();
        req.setId("req_001");
        req.setTicketNumber("F137-2024-001");
        req.setStatus("submitted");
        req.setSubmittedAt("2024-01-15T10:30:00Z");
        req.setDeliveryMethod("pickup");
        req.setEstimatedCompletion("2024-01-22T17:00:00Z");
        req.setLearnerName("Juan Dela Cruz");
        req.setLearnerReferenceNumber("123456789012");
        req.setRequestType("Original Copy");
        req.setRequesterEmail("maria@email.com");
        req.setRequesterName("Maria Dela Cruz");
        req.setComments(java.util.List.of(comment));

        when(repository.findAll()).thenReturn(java.util.List.of(req));
        when(repository.findById("req_001")).thenReturn(java.util.Optional.of(req));
    }

    @State("request does not exist")
    public void requestDoesNotExist() {
        when(repository.findById("nonexistent")).thenReturn(java.util.Optional.empty());
    }

    @State("request exists")
    public void requestExists() {
        userHasRequests();
    }

    @State("request exists and accepts comments")
    public void requestExistsAndAcceptsComments() {
        userHasRequests();
        when(repository.save(any(Form137Request.class))).thenAnswer(i -> i.getArgument(0));
    }
}
