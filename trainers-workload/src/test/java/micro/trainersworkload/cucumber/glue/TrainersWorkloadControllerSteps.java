package micro.trainersworkload.cucumber.glue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import micro.trainersworkload.dto.TrainerWorkloadRequestDTO;
import micro.trainersworkload.model.Month;
import micro.trainersworkload.model.MonthEnum;
import micro.trainersworkload.model.Workload;
import micro.trainersworkload.model.Year;
import micro.trainersworkload.repository.WorkloadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
public class TrainersWorkloadControllerSteps {

  private final String url = "http://localhost:8081/workload/trainer";

  private Response lastResponse;

  @Autowired
  private WorkloadRepository workloadRepository;

  @Given("exist workload for trainer with username {string}")
  public void existWorkloadForTrainerWithUsername(String username) {
    Month month = Month.builder()
        .month(MonthEnum.JANUARY)
        .summaryDuration(60)
        .build();

    Year year = Year.builder()
        .year(2025)
        .months(List.of(month))
        .build();

    Workload workload = new Workload(null, username,
        "John", "Doe",
        true, List.of(year));

    workloadRepository.save(workload);
  }

  @When("the client requests the monthly workload for {string}")
  public void theClientRequestsTheMonthlyWorkloadFor(String username) {
    TrainerWorkloadRequestDTO workloadRequestDTO = new TrainerWorkloadRequestDTO(username, 2025, 1);
    lastResponse = given()
        .contentType("application/json")
        .body(workloadRequestDTO)
        .when().get(url + "/monthly-workload");
  }

  @Then("the response status should be {int}")
  public void theResponseStatusShouldBe(int statusCode) {
    lastResponse.then().statusCode(statusCode);
  }

  @And("the response body should contain workload details and duration {int}")
  public void theResponseBodyShouldContainWorkloadDetailsAndDuration(int duration) {
    lastResponse.then()
        .body("workload", equalTo(duration));
  }
}
