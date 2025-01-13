package micro.trainersworkload.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
      @Bean
      @LoadBalanced
      public RestTemplate restTemplate() {
            return new RestTemplate();
      }

      @Bean(name = "transactionManager")
      @Primary
      public PlatformTransactionManager dbTransactionManager(LocalContainerEntityManagerFactoryBean dbEntityManager) {
            JpaTransactionManager transactionManager
                    = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(
                    dbEntityManager.getObject());
            return transactionManager;
      }
}
