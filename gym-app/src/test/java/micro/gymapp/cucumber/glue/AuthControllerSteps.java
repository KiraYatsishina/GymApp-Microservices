package micro.gymapp.cucumber.glue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import micro.gymapp.dto.UserDTO;
import micro.gymapp.model.Role;
import micro.gymapp.model.User;
import micro.gymapp.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthControllerSteps {

  @Autowired
  private UserRepository userRepository;

  private final String url = "http://localhost:8080/gym-app";

  private Response lastResponse;
  private String lastJwt;

  @Given("A user with username {} and password {} exists in the UserRepository")
  public void aUserWithUsernameAndPasswordExistsInTheUserRepository(String username, String password) {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    String hashedPassword = bCryptPasswordEncoder.encode(password);

    User user = new User();
    user.setUsername(username);
    user.setPassword(hashedPassword);
    user.setRole(Role.ROLE_TRAINEE);
    userRepository.save(user);

    Optional<User> userOptional = userRepository.findByUsername(username);

    assertTrue(userOptional.isPresent(), "User should exist in the repository");
    assertNotNull(username);
  }

  @When("The user attempts to log in with username {} and password {}")
  public void theUserAttemptsToLogInWithUsernameAndPassword(String username, String password) {
    RestAssured.baseURI = url;
    UserDTO userDTO = UserDTO.builder().username(username).password(password).build();

    lastResponse = RestAssured.given()
        .contentType(ContentType.JSON)
        .body(userDTO)
        .when()
        .post("/token");

    lastJwt = lastResponse.getBody().asString();
  }

  @Then("A JWT token is generated and returned")
  public void aJWTTokenIsGeneratedAndReturned() {
    assertNotNull(lastJwt);
    Assertions.assertFalse(lastJwt.isEmpty(), "JWT token should not be empty");
  }

  @And("The response status is HTTP {int}")
  public void theResponseStatusIsHTTP(int responseStatus) {
    assertEquals(responseStatus, lastResponse.statusCode());
  }

  @Given("The user with username {} is blocked in the LoginAttemptService")
  public void theUserWithUsernameIsBlockedInTheLoginAttemptService(String username) {
    UserDTO userDTO = UserDTO.builder()
        .username(username)
        .password("invalidPassword")
        .build();

    for (int i = 0; i < 3; i++) {
      lastResponse = RestAssured.given()
          .contentType(ContentType.JSON)
          .body(userDTO)
          .when()
          .post("/token");
    }
  }

  @Then("The response message is {string}")
  public void theResponseMessageIs(String message) {
    assertEquals(message, lastResponse.getBody().asString());
  }
}
