package micro.gymapp.cucumber.glue;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import micro.gymapp.dto.CreateTrainingDTO;
import micro.gymapp.model.User;
import micro.gymapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TrainerProducerSteps {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JmsTemplate jmsTemplate;

  private final String url = "http://localhost:8080/gym-app";
  private Response lastResponse;

  @Given("A trainee {string} exists in the system")
  public void aTraineeExistsInTheSystem(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    assertTrue(user.isPresent(), "Trainee should exist in the system");
  }

  @Given("A trainer {string} exists in the system")
  public void aTrainerExistsInTheSystem(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    assertTrue(user.isPresent(), "Trainer should exist in the system");
  }

  @When("The trainer schedules a training session for {string}")
  public void theTrainerSchedulesATrainingSession(String username) {
    CreateTrainingDTO dto = new CreateTrainingDTO();
    dto.setTraineeUsername(username);
    dto.setTrainerUsername("Alice.Cooper");
    dto.setName("Strength Training");
    dto.setDate(LocalDate.of(2025,03,02));
    dto.setType("FITNESS");
    dto.setDuration(60);

    lastResponse = RestAssured.given()
        .baseUri(url)
        .contentType(ContentType.JSON)
        .body(dto)
        .when()
        .post("/trainer/addTraining");
  }

  @Then("The message queue should receive a notification")
  public void theMessageQueueShouldReceiveANotification() {
    assertNotNull(lastResponse);
    assertEquals(200, lastResponse.statusCode());

    Message message = jmsTemplate.receive("training.queue");

    assertNotNull(message, "No message received from ActiveMQ!");
    assertTrue(message instanceof TextMessage);

    try {
      String messageBody = ((TextMessage) message).getText();
      System.out.println("Received message: " + messageBody);
      assertTrue(messageBody.contains("Alice.Cooper"));
    } catch (Exception e) {
      fail("Failed to read message from queue: " + e.getMessage());
    }
  }
}
