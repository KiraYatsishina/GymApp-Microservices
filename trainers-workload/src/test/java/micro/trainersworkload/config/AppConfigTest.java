package micro.trainersworkload.config;

import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class AppConfigTest {

      private final AppConfig appConfig = new AppConfig();

      @Test
      void testRestTemplateBeanCreation() {
            RestTemplate restTemplate = appConfig.restTemplate();

            assertNotNull(restTemplate, "RestTemplate should not be null");
            assertTrue(restTemplate.getInterceptors().isEmpty(), "RestTemplate should not have interceptors by default");
      }

      @Test
      void testDbTransactionManagerCreation() {
            LocalContainerEntityManagerFactoryBean mockEntityManagerFactory = mock(LocalContainerEntityManagerFactoryBean.class);
            MockitoAnnotations.openMocks(this);

            PlatformTransactionManager transactionManager = appConfig.dbTransactionManager(mockEntityManagerFactory);

            assertNotNull(transactionManager, "TransactionManager should not be null");
            assertTrue(transactionManager instanceof JpaTransactionManager, "TransactionManager should be an instance of JpaTransactionManager");
      }
}
