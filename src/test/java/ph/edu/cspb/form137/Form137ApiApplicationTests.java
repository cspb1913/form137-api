package ph.edu.cspb.form137;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ph.edu.cspb.form137.repository.Form137RequestRepository;
import ph.edu.cspb.form137.repository.CommentRepository;

/**
 * Basic context load test.
 * <p>Embedded MongoDB auto-configuration is disabled to avoid
 * downloading the MongoDB binary during tests, which can fail in
 * restricted environments.</p>
 */
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
                "de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration," +
                "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration," +
                "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
        "auth.enabled=false",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri="
})
class Form137ApiApplicationTests {

    @MockBean
    Form137RequestRepository repository;

    @MockBean
    CommentRepository commentRepository;

    @Test
    void contextLoads() {
    }
}
