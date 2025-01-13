package micro.gymapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GymApplicationTest {

      @Test
      void contextLoads() {
      }

      @Test
      void annotations_ShouldBePresent() {
            Class<GymApplication> clazz = GymApplication.class;

            assertTrue(clazz.isAnnotationPresent(SpringBootApplication.class),
                    "@SpringBootApplication should be present on GymApplication");
            assertTrue(clazz.isAnnotationPresent(EnableDiscoveryClient.class),
                    "@EnableDiscoveryClient should be present on GymApplication");
            assertTrue(clazz.isAnnotationPresent(EnableFeignClients.class),
                    "@EnableFeignClients should be present on GymApplication");
      }
}