package micro.gymapp.healthTest;


import micro.gymapp.health.ExternalApiHealthIndicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExternalApiHealthIndicatorTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExternalApiHealthIndicator healthIndicator;

    private static final String EXTERNAL_API_URL = "https://example.com/api/status";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHealthUpWhenExternalApiIsAvailable() {
        when(restTemplate.getForObject(EXTERNAL_API_URL, String.class)).thenReturn("OK");

        Health health = healthIndicator.health();

        assertEquals(Health.up().withDetail("External API", "Available").build(), health);
        verify(restTemplate, times(1)).getForObject(EXTERNAL_API_URL, String.class);
    }

    @Test
    void testHealthDownWhenExternalApiReturnsNull() {
        when(restTemplate.getForObject(EXTERNAL_API_URL, String.class)).thenReturn(null);

        Health health = healthIndicator.health();

        assertEquals(Health.down().withDetail("External API", "Unavailable").build(), health);
        verify(restTemplate, times(1)).getForObject(EXTERNAL_API_URL, String.class);
    }

    @Test
    void testHealthDownWhenExternalApiThrowsException() {
        String errorMessage = "Connection timed out";
        when(restTemplate.getForObject(EXTERNAL_API_URL, String.class)).thenThrow(new RestClientException(errorMessage));

        Health health = healthIndicator.health();

        String truncatedMessage = errorMessage.length() > 100 ? errorMessage.substring(0, 100) + "..." : errorMessage;
        assertEquals(
                Health.down()
                        .withDetail("External API", "Unavailable")
                        .withDetail("Error", truncatedMessage)
                        .build(),
                health
        );
        verify(restTemplate, times(1)).getForObject(EXTERNAL_API_URL, String.class);
    }
}
