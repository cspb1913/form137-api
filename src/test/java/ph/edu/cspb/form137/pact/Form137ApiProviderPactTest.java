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
@Provider("Form137API")
@PactFolder("pacts")
class Form137ApiProviderPactTest {

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

    @State("API is available for form submission")
    public void apiAvailableForSubmission() {
        Form137Request saved = new Form137Request();
        saved.setTicketNumber("REQ-2025-00123");
        saved.setSubmittedAt("2025-01-11T21:52:11.000Z");
        when(repository.save(any(Form137Request.class))).thenReturn(saved);
    }

    @State("API is available for self-requester form submission")
    public void apiAvailableForSelfSubmission() {
        Form137Request saved = new Form137Request();
        saved.setTicketNumber("REQ-2025-00124");
        saved.setSubmittedAt("2025-01-11T21:52:11.000Z");
        when(repository.save(any(Form137Request.class))).thenReturn(saved);
    }

    @State("API validates form data")
    public void apiValidatesFormData() {
        // no repository interaction needed
    }

    @State("A form submission exists with ticket number REQ-2025-00123")
    public void submissionExists() {
        Form137Request req = new Form137Request();
        req.setTicketNumber("REQ-2025-00123");
        req.setStatus("processing");
        req.setSubmittedAt("2025-01-11T21:52:11.000Z");
        req.setUpdatedAt("2025-01-12T09:30:00.000Z");
        req.setNotes("Documents under review");
        when(repository.findByTicketNumber("REQ-2025-00123")).thenReturn(java.util.Optional.of(req));
    }

    @State("No form submission exists with ticket number REQ-2025-99999")
    public void submissionDoesNotExist() {
        when(repository.findByTicketNumber("REQ-2025-99999")).thenReturn(java.util.Optional.empty());
    }
}
