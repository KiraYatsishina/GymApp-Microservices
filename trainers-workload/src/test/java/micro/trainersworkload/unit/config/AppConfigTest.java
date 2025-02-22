package micro.trainersworkload.unit.config;

import micro.trainersworkload.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppConfigTest {

      private final AppConfig appConfig = new AppConfig();

      @Test
      void testRestTemplateBeanCreation() {
            RestTemplate restTemplate = appConfig.restTemplate();

            assertNotNull(restTemplate, "RestTemplate should not be null");
            assertTrue(restTemplate.getInterceptors().isEmpty(), "RestTemplate should not have interceptors by default");
      }
}
