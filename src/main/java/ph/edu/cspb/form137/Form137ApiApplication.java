package ph.edu.cspb.form137;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Form137ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(Form137ApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner logEnvVars() {
		return args -> {
			Logger logger = LoggerFactory.getLogger(Form137ApiApplication.class);
			logger.info("SPRING_PROFILES_ACTIVE: {}", System.getenv("SPRING_PROFILES_ACTIVE"));
			logger.info("SPRING_DATA_MONGODB_URI: {}", System.getenv("SPRING_DATA_MONGODB_URI"));
			logger.info("SPRING_DATA_MONGODB_DATABASE: {}", System.getenv("SPRING_DATA_MONGODB_DATABASE"));
			logger.info("AUTH0_ISSUER_URI: {}", System.getenv("AUTH0_ISSUER_URI"));
			logger.info("AUTH0_AUDIENCE: {}", System.getenv("AUTH0_AUDIENCE"));
		};
	}

}
