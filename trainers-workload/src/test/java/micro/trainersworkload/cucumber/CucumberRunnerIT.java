package micro.trainersworkload.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import micro.trainersworkload.TrainersWorkloadApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@RunWith(Cucumber.class)
@CucumberContextConfiguration
@SpringBootTest(classes = {TrainersWorkloadApplication.class,
                          CucumberRunnerIT.class},
                          webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberOptions(plugin = "pretty",
    features = "src/test/resources/features")
@TestPropertySource(locations = "classpath:application-test.properties")
public class CucumberRunnerIT {

}
