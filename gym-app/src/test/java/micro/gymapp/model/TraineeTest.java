package micro.gymapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TraineeTest {

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = Trainee.builder()
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .trainers(Arrays.asList(new Trainer(), new Trainer()))
                .trainings(Collections.emptyList())
                .build();

        trainee.setUserId(1l);
        trainee.setRole(Role.ROLE_TRAINEE);
        trainee.setPassword("password01");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("John.Doe");
        trainee.setActive(true);
    }

    @Test
    void testGetFullName() {
        String expectedFullName = "John Doe";
        assertEquals(expectedFullName, trainee.getFullName());
    }

    @Test
    void testEquals_SameObject() {
        assertTrue(trainee.equals(trainee));
    }

    @Test
    void testEquals_DifferentObject() {
        Trainee anotherTrainee = Trainee.builder()
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .trainers(Arrays.asList(new Trainer(), new Trainer()))
                .build();

        anotherTrainee.setUserId(1l);
        anotherTrainee.setRole(Role.ROLE_TRAINEE);
        anotherTrainee.setPassword("password01");
        anotherTrainee.setFirstName("John");
        anotherTrainee.setLastName("Doe");
        anotherTrainee.setUsername("John.Doe");
        anotherTrainee.setActive(true);
        assertTrue(trainee.equals(anotherTrainee));
    }

    @Test
    void testEquals_DifferentUserId() {
        Trainee anotherTrainee = Trainee.builder()
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .trainers(Arrays.asList(new Trainer(), new Trainer()))
                .build();

        anotherTrainee.setUserId(2l);
        anotherTrainee.setRole(Role.ROLE_TRAINEE);
        anotherTrainee.setPassword("password01");
        anotherTrainee.setFirstName("John");
        anotherTrainee.setLastName("Doe");
        anotherTrainee.setUsername("John.Doe");
        anotherTrainee.setActive(true);

        assertFalse(trainee.equals(anotherTrainee));
    }

    @Test
    void testHashCode() {
        Trainee anotherTrainee = Trainee.builder()
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .trainers(Arrays.asList(new Trainer(), new Trainer()))
                .build();

        anotherTrainee.setUserId(1l);
        anotherTrainee.setRole(Role.ROLE_TRAINEE);
        anotherTrainee.setPassword("password01");
        anotherTrainee.setFirstName("John");
        anotherTrainee.setLastName("Doe");
        anotherTrainee.setUsername("John.Doe");
        anotherTrainee.setActive(true);
        assertEquals(trainee.hashCode(), anotherTrainee.hashCode());
    }

    @Test
    void testGetDateOfBirth() {
        LocalDate expectedDateOfBirth = LocalDate.of(1990, 5, 15);
        assertEquals(expectedDateOfBirth, trainee.getDateOfBirth());
    }

    @Test
    void testGetAddress() {
        String expectedAddress = "123 Main St";
        assertEquals(expectedAddress, trainee.getAddress());
    }

    @Test
    void testGetTrainers() {
        assertEquals(2, trainee.getTrainers().size());
    }

    @Test
    void testGetTrainings() {
        assertTrue(trainee.getTrainings().isEmpty());
    }
}
