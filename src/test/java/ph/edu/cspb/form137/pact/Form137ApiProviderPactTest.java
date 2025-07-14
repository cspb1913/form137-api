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
import org.springframework.beans.factory.annotation.Autowired;

import ph.edu.cspb.form137.controller.Form137Controller;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.autoconfigure.exclude=" +
                "de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration")
@Provider("Form137API")
@PactFolder("pacts")
class Form137ApiProviderPactTest {

    @LocalServerPort
    private int port;

    @Autowired
    Form137Controller controller;

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
        controller.setScenario("success");
    }

    @State("API is available for self-requester form submission")
    public void apiAvailableForSelfSubmission() {
        controller.setScenario("self");
    }

    @State("API validates form data")
    public void apiValidatesFormData() {
        controller.setScenario("invalid");
    }

    @State("A form submission exists with ticket number REQ-2025-00123")
    public void submissionExists() {
        controller.setScenario("success");
    }

    @State("No form submission exists with ticket number REQ-2025-99999")
    public void submissionDoesNotExist() {
        controller.setScenario("missing");
    }
}
