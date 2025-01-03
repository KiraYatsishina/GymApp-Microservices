package micro.gymapp.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SwaggerHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;

    public SwaggerHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        return checkSwaggerApi() ? Health.up().build() : Health.down().build();
    }

    private boolean checkSwaggerApi() {
        String swaggerUrl = "http://localhost:8080/GymApp/v3/api-docs";

        try {
            String response = restTemplate.getForObject(swaggerUrl, String.class);
            return response != null && response.contains("openapi");
        } catch (Exception e) {
            return false;
        }
    }
}