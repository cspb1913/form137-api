package ph.edu.cspb.form137;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic context load test.
 * <p>Embedded MongoDB auto-configuration is disabled to avoid
 * downloading the MongoDB binary during tests, which can fail in
 * restricted environments.</p>
 */
@SpringBootTest(properties =
        "spring.autoconfigure.exclude=" +
        "de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration")
class Form137ApiApplicationTests {

    @Test
    void contextLoads() {
    }
}
