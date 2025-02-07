package micro.gymapp.unit.configTest;

import micro.gymapp.config.WebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

      @InjectMocks
      private WebConfig webConfig;

      @Mock
      private ResourceHandlerRegistry registry;

      @Value("${spring.mvc.view.prefix}")
      private String prefix = "/WEB-INF/views/";

      @Value("${spring.mvc.view.suffix}")
      private String suffix = ".jsp";

      @BeforeEach
      void setUp() {
            MockitoAnnotations.openMocks(this);
            webConfig = new WebConfig();
      }

      @Test
      void setupViewResolver_ShouldReturnCorrectViewResolver() {
            InternalResourceViewResolver resolver = webConfig.setupViewResolver();
            assertNotNull(resolver, "ViewResolver should be initialized");
      }

}
