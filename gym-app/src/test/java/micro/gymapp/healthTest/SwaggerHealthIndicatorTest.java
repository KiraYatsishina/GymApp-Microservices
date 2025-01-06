package micro.gymapp.healthTest;


import micro.gymapp.health.SwaggerHealthIndicator;
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

class SwaggerHealthIndicatorTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SwaggerHealthIndicator swaggerHealthIndicator;

    private static final String SWAGGER_URL = "http://localhost:8080/GymApp/v3/api-docs";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHealthUpWhenSwaggerApiIsAvailable() {
        when(restTemplate.getForObject(SWAGGER_URL, String.class)).thenReturn("{\"openapi\":\"3.0.1\"}");

        Health health = swaggerHealthIndicator.health();

        assertEquals(Health.up().build(), health);
        verify(restTemplate, times(1)).getForObject(SWAGGER_URL, String.class);
    }

    @Test
    void testHealthDownWhenSwaggerApiReturnsNull() {
        when(restTemplate.getForObject(SWAGGER_URL, String.class)).thenReturn(null);

        Health health = swaggerHealthIndicator.health();

        assertEquals(Health.down().build(), health);
        verify(restTemplate, times(1)).getForObject(SWAGGER_URL, String.class);
    }

    @Test
    void testHealthDownWhenSwaggerApiResponseDoesNotContainOpenApi() {
        when(restTemplate.getForObject(SWAGGER_URL, String.class)).thenReturn("{\"info\":\"Swagger Docs\"}");

        Health health = swaggerHealthIndicator.health();

        assertEquals(Health.down().build(), health);
        verify(restTemplate, times(1)).getForObject(SWAGGER_URL, String.class);
    }

    @Test
    void testHealthDownWhenSwaggerApiThrowsException() {
        when(restTemplate.getForObject(SWAGGER_URL, String.class)).thenThrow(new RestClientException("Connection refused"));

        Health health = swaggerHealthIndicator.health();

        assertEquals(Health.down().build(), health);
        verify(restTemplate, times(1)).getForObject(SWAGGER_URL, String.class);
    }
}
