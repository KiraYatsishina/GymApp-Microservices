package micro.gymapp.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import micro.gymapp.GymApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@RunWith(Cucumber.class)
@CucumberContextConfiguration
@SpringBootTest(classes = {GymApplication.class,
    CucumberRunnerIT.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberOptions(plugin = "pretty",
                features = "src/test/resources/features")
@TestPropertySource(properties = "spring.config.location=classpath:/application-test.yml")
public class CucumberRunnerIT {

}

