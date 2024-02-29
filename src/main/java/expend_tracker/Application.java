package expend_tracker;

import expend_tracker.controller.UserPageController;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {

    static {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("spring.datasource.url", dotenv.get("DATABASE_URL"));
        System.setProperty("spring.datasource.username", dotenv.get("DATABASE_USER"));
        System.setProperty("spring.datasource.password", dotenv.get("DATABASE_PASSWORD"));
        System.setProperty("okta.oauth2.issuer", dotenv.get("OKTA_ISSUER"));
        System.setProperty("auth0.api.url", dotenv.get("OKTA_URL"));
        System.setProperty("auth0.client.id", dotenv.get("OKTA_CLIENT_ID"));
        System.setProperty("auth0.client.secret", dotenv.get("OKTA_CLIENT_SECRET"));
        System.setProperty("ai.api.key", dotenv.get("AI_API_KEY"));
    }

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

    }


}
