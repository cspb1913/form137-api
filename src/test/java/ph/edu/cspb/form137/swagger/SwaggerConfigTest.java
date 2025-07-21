package ph.edu.cspb.form137.swagger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import ph.edu.cspb.form137.repository.Form137RequestRepository;
import ph.edu.cspb.form137.config.OpenApiConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = OpenApiConfig.class)
@ActiveProfiles("test")
public class SwaggerConfigTest {

    @MockBean
    private Form137RequestRepository repository;

    @Autowired
    private OpenApiConfig openApiConfig;

    @Test
    public void shouldCreateOpenAPIConfiguration() {
        var openAPI = openApiConfig.form137OpenAPI();
        
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Form 137 API");
        assertThat(openAPI.getInfo().getDescription()).isEqualTo("API for managing Form 137 transcript requests");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("CSPB Form 137 System");
        assertThat(openAPI.getInfo().getContact().getEmail()).isEqualTo("support@cspb.edu.ph");
        
        // Verify security scheme is configured
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
        
        var bearerAuth = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(bearerAuth.getType().toString()).isEqualTo("http");
        assertThat(bearerAuth.getScheme()).isEqualTo("bearer");
        assertThat(bearerAuth.getBearerFormat()).isEqualTo("JWT");
    }
}