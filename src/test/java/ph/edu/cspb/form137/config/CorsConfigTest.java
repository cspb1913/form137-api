package ph.edu.cspb.form137.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorsConfigTest {

    @Test
    void shouldDefineForm137OriginConstant() {
        // Verify the constant is properly defined
        assertNotNull(CorsConfig.FORM137_ORIGIN);
        assertEquals("https://form137.cspb.edu.ph", CorsConfig.FORM137_ORIGIN);
    }

    @Test
    void shouldCreateCorsConfigurer() {
        // Given
        CorsConfig corsConfig = new CorsConfig();
        
        // When
        var configurer = corsConfig.corsConfigurer();
        
        // Then
        assertNotNull(configurer);
    }
}