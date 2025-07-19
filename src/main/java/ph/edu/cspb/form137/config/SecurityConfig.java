package ph.edu.cspb.form137.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${auth.enabled:false}")
    private boolean authEnabled;

    @Value("${auth0.audience:}")
    private String audience;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if (authEnabled) {
            http
                .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/api/health/**").permitAll()
                    .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                        .audience(audience)
                        .and()
                        .jwt(Customizer.withDefaults())
                    )
                )
                .cors(Customizer.withDefaults());
        } else {
            http
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
        }
        return http.build();
    }
}
