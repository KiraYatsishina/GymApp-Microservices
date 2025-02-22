package micro.gymapp.cucumber.glue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import micro.gymapp.dto.Trainee.SignupTrainee;
import micro.gymapp.dto.Trainee.TraineeDTO;
import micro.gymapp.dto.Trainee.UpdateTrainersListForm;
import micro.gymapp.dto.Trainer.SignupTrainer;
import micro.gymapp.model.*;
import micro.gymapp.repository.TraineeRepository;
import micro.gymapp.repository.TrainerRepository;
import micro.gymapp.repository.TrainingRepository;
import micro.gymapp.repository.TrainingTypeRepository;
import micro.gymapp.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TraineeControllerSteps {

  private final String url = "http://localhost:8080/gym-app/trainee";
  private final String urlTrainer = "http://localhost:8080/gym-app/trainer";

  private Response lastResponse;

  @Autowired
  private TraineeService traineeService;

  @Autowired
  private TraineeRepository traineeRepository;

  @Autowired
  private TrainerRepository trainerRepository;

  @Autowired
  private TrainingTypeRepository trainingTypeRepository;

  @Autowired
  private TrainingRepository trainingRepository;

  @When("the trainee submits the signup request with firstname {string} and lastname {string}")
  public void the_trainee_submits_the_signup_request_with_firstname_and_lastname(String firstname, String lastname) {
    SignupTrainee signupTrainee = new SignupTrainee(firstname, lastname,
        LocalDate.of(2005, 12,12), "address");

    lastResponse = given()
        .contentType("application/json")
        .body(signupTrainee)
        .when().post(url + "/signup");
  }

  @And("the response body should contain the created user details")
  public void theResponseBodyShouldContainTheCreatedUserDetails() {
    lastResponse.then().body("username", equalTo("John.Doe"))
        .body("password", notNullValue());
  }

  @When("the trainee submits the signup request with no firstname and lastname {string}")
  public void theTraineeSubmitsTheSignupRequestWithNoFirstnameAndLastname(String lastname) {
    SignupTrainee signupTrainee = new SignupTrainee(null, lastname,
        LocalDate.of(2005, 12,12), "address");

    lastResponse = given()
        .contentType("application/json")
        .body(signupTrainee)
        .when().post(url + "/signup");
  }

  @When("the trainee submits the signup request with no last name and firstname {string}")
  public void theTraineeSubmitsTheSignupRequestWithNoLastNameAndFirstname(String firstname) {
    SignupTrainee signupTrainee = new SignupTrainee(firstname, null,
        LocalDate.of(2005, 12,12), "address");

    lastResponse = given()
        .contentType("application/json")
        .body(signupTrainee)
        .when().post(url + "/signup");
  }

  @And("the response message should be {string}")
  public void theResponseMessageShouldBe(String message) {
    lastResponse.then().body(equalTo(message));
  }

  @And("a trainee exist with username {string}")
  public void aTraineeIsExistWithUsername(String username) {
    Optional<TraineeDTO> traineeDTOOptional = traineeService.findByUsername(username);
    assertTrue(traineeDTOOptional.isPresent());
  }

  @When("the trainee requests their profile John")
  public void theTraineeRequestsTheirProfileJohn() {
    String username = "John.Doe";

    lastResponse = given()
        .queryParam("username", username)
        .when()
        .get(url + "/myProfile");
  }

  @When("the trainee requests their profile with not existing username")
  public void theTraineeRequestsTheirProfile() {
    String username = "NotExistingUser";

    lastResponse = given()
        .queryParam("username", username)
        .when()
        .get(url + "/myProfile");
  }

  @And("the response body should contain the trainee profile information")
  public void theResponseBodyShouldContainTheTraineeProfileInformation() {
    lastResponse.then().body("username", equalTo("John.Doe"));
  }

  @And("the trainee provides updated first name {string} and last name {string}")
  public void theTraineeProvidesUpdatedFirstNameAndLastName(String firstname, String lastname) {
    lastResponse = given()
        .contentType("application/json")
        .body("{ \"firstname\": \"" + firstname + "\", \"lastname\": \"" + lastname + "\" }")
        .when().put(url + "/profile");
  }

  @Given("signup request with firstname {string} and lastname {string}")
  public void signupRequestWithFirstnameAndLastname(String firstname, String lastname) {
    SignupTrainee signupTrainee = new SignupTrainee(firstname, lastname,
        LocalDate.of(2005, 12,12), "address");

    lastResponse = given()
        .contentType("application/json")
        .body(signupTrainee)
        .when().post(url + "/signup");

    aTraineeIsExistWithUsername(firstname + "." + lastname);
  }

  @When("the trainee submits the update profile request firstname {string} and lastname {string}")
  public void theTraineeSubmitsTheUpdateProfileRequestFirstNameAndLastName(String firstname, String lastname) {

    lastResponse = given()
        .contentType("application/json")
        .body("{" +
            "\"username\": \"Kira.Doe\"," +
            "\"firstName\": \"" + firstname + "\"," +
            "\"lastName\": \"" + lastname + "\"}" )
        .when()
        .put(url + "/updateProfile");
  }

  @And("the response body should contain the updated profile details")
  public void theResponseBodyShouldContainTheUpdatedProfileDetails() {
    lastResponse.then().body("firstName", equalTo("Mary"))
        .body("lastName", equalTo("Smith"));
  }

  @And("the trainee provides an empty first name")
  public void theTraineeProvidesAnEmptyFirstName() {
    lastResponse = given()
        .contentType("application/json")
        .body("{ \"firstname\": \"\", \"lastname\": \"Doe\" }")
        .when().put(url + "/profile");
  }

  @When("the trainee with username {string} requests the list of unassigned trainers")
  public void theTraineeWithUsernameRequestsTheListOfUnassignedTrainers(String username) {
    lastResponse = given()
        .queryParam("username", username)
        .when().get(url + "/notAssignedTrainersList");
  }

  @And("the response body must not contain trainers")
  public void theResponseBodyMustNotContainTrainers() {
    lastResponse.then().body("$", empty());
  }

  @And("no unassigned trainers exist")
  public void noUnassignedTrainersExist() {
    lastResponse.then().body("trainers", empty());
  }

  @And("the trainee provides a list of trainer usernames [{string}, {string}]")
  public void theTraineeProvidesAListOfTrainerUsernames(String trainer1, String trainer2) {
    lastResponse = given()
        .contentType("application/json")
        .body("{ \"trainers\": [\"" + trainer1 + "\", \"" + trainer2 + "\"] }")
        .when().put(url + "/update-trainers");
  }

  @And("the response body should contain the updated list of trainers [{string}]")
  public void theResponseBodyShouldContainTheUpdatedListOfTrainers(String trainer1) {
    lastResponse.then().body("trainers", hasItem(trainer1));
  }

  @When("the trainee with username {string} submits the delete profile request")
  public void theTraineeSubmitsTheDeleteProfileRequest(String username) {
    lastResponse = given()
        .queryParam("username", username)
        .when().delete(url + "/delete");
  }

  @When("the trainee requests their training list")
  public void theTraineeRequestsTheirTrainingList() {
    lastResponse = given()
        .when().get(url + "/training-list");
  }

  @And("the response body should contain a list of trainings")
  public void theResponseBodyShouldContainAListOfTrainings() {
    lastResponse.then().body("trainings", not(empty()));
  }

  @When("the trainee requests their training list with no trainings")
  public void theTraineeRequestsTheirTrainingListWithNoTrainings() {
    lastResponse = given()
        .when().get(url + "/training-list");
  }

  @Then("the response status should be {int}")
  public void theResponseStatusShouldBe(int statusCode) {
    lastResponse.then().statusCode(statusCode);
  }

  @When("the trainee submits the update profile request for {string}, lastname {string}")
  public void theTraineeSubmitsTheUpdateProfileRequestForLastname(String username, String lastname) {
    lastResponse = given()
        .contentType("application/json")
        .body("{\"username\": \"" + username + "\", \"lastName\": \"" + lastname + "\"}")
        .when()
        .put(url + "/updateProfile");
  }

  @Given("a trainee John Doe has one trainer")
  public void aTraineeHasOneTrainer() {
    Optional<Trainer> trainerDTOOptional = trainerRepository.findByUsername("Lina.Doe");
    assertTrue(trainerDTOOptional.isPresent());
    Trainer trainer = trainerDTOOptional.get();

    Trainee johnDoeTrainee = traineeRepository.findByUsername("John.Doe").get();
    johnDoeTrainee.setTrainers(List.of(trainer));
    traineeRepository.save(johnDoeTrainee);
  }

  @And("the response body must contain one trainer")
  public void theResponseBodyMustContainOneTrainer() {
    lastResponse.then()
        .body("$", hasSize(1))
        .body("[0].username", equalTo("Lina.Doe"));
  }

  @Given("create trainer with firstname {string} and lastname {string}")
  public void createTrainerWithFirstnameAndLastname(String firstname, String lastname) {
    if(!trainingTypeRepository.existsById(1L))
      trainingTypeRepository.save(new TrainingType(null, TrainingTypeEnum.FITNESS, null, null));

    SignupTrainer signupTrainer = new SignupTrainer(firstname, lastname, "FITNESS");

    lastResponse = given()
        .contentType("application/json")
        .body(signupTrainer)
        .when().post(urlTrainer + "/signup");

    Optional<Trainer> trainerDTOOptional = trainerRepository.findByUsername(firstname + "." + lastname);
    assertTrue(trainerDTOOptional.isPresent());
  }

  @When("the trainee with username {string} submits the update trainers with usernames:")
  public void theTraineeWithUsernameSubmitsTheUpdateTrainersListWith(String username, List<String> listOfTrainerUsernames) {;
    UpdateTrainersListForm form = new UpdateTrainersListForm(username, listOfTrainerUsernames);

    lastResponse = given()
        .contentType("application/json")
        .body(form)
        .when().put(url + "/updateTrainersList");
  }

  @And("the response body should contain the updated list of trainers with usernames:")
  public void theResponseBodyShouldContainTheUpdatedListOfTrainersWithUsernames(List<String> listOfTrainerUsernames) {
    List<String> actualUsernames = lastResponse.jsonPath().getList("username", String.class);

    assertThat(actualUsernames)
        .hasSize(2)
        .containsExactlyInAnyOrderElementsOf(listOfTrainerUsernames);
  }

  @Given("the trainee with username {string} has three trainings")
  public void theTraineeWithUsernameHasThreeTrainings(String username) {
    Trainee trainee = traineeRepository.findByUsername("John.Doe").get();
    Trainer trainer = trainerRepository.findByUsername("Charlie.Brown").get();
    TrainingType trainingTypeEnum = trainingTypeRepository.findByTrainingTypeName(TrainingTypeEnum.FITNESS).get();

    for (int i = 1; i < 4; i++) {
      Training training = Training.builder()
          .trainee(trainee)
          .trainer(trainer)
          .trainingName("training" + i)
          .trainingType(trainingTypeEnum)
          .duration(60)
          .trainingDate(LocalDate.of(2025, 1, i))
          .build();

      trainingRepository.save(training);
    }
  }

  @When("the trainee with username {string} requests their training list")
  public void theTraineeWithUsernameRequestsTheirTrainingList(String username) {
    lastResponse = given()
        .queryParam("username", username)
        .when()
        .get(url + "/trainingList");
  }

  @And("the response body should contain a list of {int} trainings")
  public void theResponseBodyShouldContainAListOfThreeTrainings(int size) {
    lastResponse.then()
        .body("$.size()", equalTo(size));
  }
}
