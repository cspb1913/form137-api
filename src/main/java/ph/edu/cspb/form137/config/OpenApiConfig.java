package ph.edu.cspb.form137.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI form137OpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Form 137 API")
                        .description("API for managing Form 137 transcript requests")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CSPB Form 137 System")
                                .email("support@cspb.edu.ph")))
                .servers(List.of(
                        new Server().url("/").description("Current server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT authentication token")));
    }
}