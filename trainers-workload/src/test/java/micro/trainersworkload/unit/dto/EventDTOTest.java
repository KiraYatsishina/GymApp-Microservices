package micro.trainersworkload.unit.dto;

import micro.trainersworkload.dto.ActionEnum;
import micro.trainersworkload.dto.EventDTO;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EventDTOTest {

  @Test
  void testNoArgsConstructor() {
    EventDTO event = new EventDTO();
    assertNotNull(event);
  }

  @Test
  void testAllArgsConstructor() {
    LocalDate trainingDate = LocalDate.of(2025, 1, 22);
    EventDTO event = new EventDTO(
        "testUser",
        "John",
        "Doe",
        true,
        trainingDate,
        60,
        ActionEnum.ADD
    );

    assertEquals("testUser", event.getUsername());
    assertEquals("John", event.getFirstName());
    assertEquals("Doe", event.getLastName());
    assertTrue(event.isStatus());
    assertEquals(trainingDate, event.getTrainingDate());
    assertEquals(60, event.getTrainingDuration());
    assertEquals(ActionEnum.ADD, event.getAction());
  }

  @Test
  void testSettersAndGetters() {
    EventDTO event = new EventDTO();
    LocalDate trainingDate = LocalDate.of(2025, 1, 22);

    event.setUsername("testUser");
    event.setFirstName("John");
    event.setLastName("Doe");
    event.setStatus(true);
    event.setTrainingDate(trainingDate);
    event.setTrainingDuration(60);
    event.setAction(ActionEnum.ADD);

    assertEquals("testUser", event.getUsername());
    assertEquals("John", event.getFirstName());
    assertEquals("Doe", event.getLastName());
    assertTrue(event.isStatus());
    assertEquals(trainingDate, event.getTrainingDate());
    assertEquals(60, event.getTrainingDuration());
    assertEquals(ActionEnum.ADD, event.getAction());
  }

  @Test
  void testEqualsAndHashCode() {
    LocalDate trainingDate = LocalDate.of(2025, 1, 22);

    EventDTO event1 = new EventDTO(
        "testUser",
        "John",
        "Doe",
        true,
        trainingDate,
        60,
        ActionEnum.ADD
    );

    EventDTO event2 = new EventDTO(
        "testUser",
        "John",
        "Doe",
        true,
        trainingDate,
        60,
        ActionEnum.ADD
    );

    assertEquals(event1, event2);
    assertEquals(event1.hashCode(), event2.hashCode());
  }

  @Test
  void testToString() {
    LocalDate trainingDate = LocalDate.of(2025, 1, 22);
    EventDTO event = new EventDTO(
        "testUser",
        "John",
        "Doe",
        true,
        trainingDate,
        60,
        ActionEnum.ADD
    );

    String expected = "EventDTO(username=testUser, firstName=John, lastName=Doe, status=true, trainingDate=2025-01-22, trainingDuration=60, action=ADD)";
    assertEquals(expected, event.toString());
  }
}

