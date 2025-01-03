package micro.gymapp.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalApiHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;
    private static final String EXTERNAL_API_URL = "https://example.com/api/status";

    public ExternalApiHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            String response = restTemplate.getForObject(EXTERNAL_API_URL, String.class);
            return response != null ? Health.up().withDetail("External API", "Available").build()
                    : Health.down().withDetail("External API", "Unavailable").build();
        } catch (Exception e) {
            String shortMessage = e.getMessage().length() > 100 ? e.getMessage().substring(0, 100) + "..." : e.getMessage();
            return Health.down()
                    .withDetail("External API", "Unavailable")
                    .withDetail("Error", shortMessage)
                    .build();
        }
    }
}
