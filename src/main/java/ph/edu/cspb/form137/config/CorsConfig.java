package ph.edu.cspb.form137.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
public class CorsConfig {

    /**
     * Primary allowed origin for the Form 137 application
     */
    public static final String FORM137_ORIGIN = "https://form137.cspb.edu.ph";

    @Value("${cors.allowed-origins:}")
    private String[] additionalOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Ensure FORM137_ORIGIN is always included in allowed origins
                Set<String> allowedOrigins = new LinkedHashSet<>();
                allowedOrigins.add(FORM137_ORIGIN);
                
                // Add any additional origins from configuration
                if (additionalOrigins != null && additionalOrigins.length > 0) {
                    Arrays.stream(additionalOrigins)
                        .filter(origin -> origin != null && !origin.trim().isEmpty())
                        .forEach(allowedOrigins::add);
                }
                
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.toArray(new String[0]))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}
