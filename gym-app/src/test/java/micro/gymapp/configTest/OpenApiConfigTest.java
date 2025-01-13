package micro.gymapp.configTest;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import micro.gymapp.config.OpenApiConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

      private final OpenApiConfig openApiConfig = new OpenApiConfig();

      @Test
      void testApiConfiguration() {
            OpenAPI openAPI = openApiConfig.api();

            assertNotNull(openAPI, "OpenAPI should not be null");
            assertNotNull(openAPI.getInfo(), "API Info should not be null");
            assertNotNull(openAPI.getServers(), "Servers list should not be null");
            assertEquals("Gym API", openAPI.getInfo().getTitle(), "API title should be 'Gym API'");

            List<Server> servers = openAPI.getServers();
            assertEquals(1, servers.size(), "There should be exactly one server defined");
            assertEquals("http://localhost:8080", servers.get(0).getUrl(), "Server URL should be 'http://localhost:8080'");
      }
}
