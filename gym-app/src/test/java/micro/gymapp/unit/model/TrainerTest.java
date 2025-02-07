package micro.gymapp.unit.model;

import micro.gymapp.model.Role;
import micro.gymapp.model.Trainer;
import micro.gymapp.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TrainerTest {

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = Trainer.builder()
                .specialization(new TrainingType())
                .trainings(Collections.emptyList())
                .trainees(Collections.emptyList())
                .build();

        trainer.setUserId(1L);
        trainer.setRole(Role.ROLE_TRAINER);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setUsername("Jane.Smith");
        trainer.setPassword("password01");
        trainer.setActive(true);
    }

    @Test
    void testGetFullName() {
        String expectedFullName = "Jane Smith";
        assertEquals(expectedFullName, trainer.getFullName());
    }

    @Test
    void testEquals_SameObject() {
        assertTrue(trainer.equals(trainer));
    }

    @Test
    void testEquals_DifferentObject() {
        Trainer anotherTrainer = Trainer.builder()
                .specialization(new TrainingType())
                .trainings(Collections.emptyList())
                .trainees(Collections.emptyList())
                .build();

        anotherTrainer.setUserId(1L);
        anotherTrainer.setRole(Role.ROLE_TRAINER);
        anotherTrainer.setFirstName("Jane");
        anotherTrainer.setLastName("Smith");
        anotherTrainer.setUsername("Jane.Smith");
        anotherTrainer.setPassword("password01");
        anotherTrainer.setActive(true);

        assertTrue(trainer.equals(anotherTrainer));
    }

    @Test
    void testEquals_DifferentUserId() {
        Trainer anotherTrainer = Trainer.builder()
                .specialization(new TrainingType())
                .trainings(Collections.emptyList())
                .trainees(Collections.emptyList())
                .build();

        anotherTrainer.setUserId(2L);
        anotherTrainer.setRole(Role.ROLE_TRAINER);
        anotherTrainer.setFirstName("Jane");
        anotherTrainer.setLastName("Smith");
        anotherTrainer.setUsername("Jane.Smith");
        anotherTrainer.setPassword("password01");
        anotherTrainer.setActive(true);

        assertFalse(trainer.equals(anotherTrainer));
    }

    @Test
    void testHashCode() {
        Trainer anotherTrainer = Trainer.builder()
                .specialization(new TrainingType())
                .trainings(Collections.emptyList())
                .trainees(Collections.emptyList())
                .build();

        anotherTrainer.setUserId(1L);
        anotherTrainer.setRole(Role.ROLE_TRAINER);
        anotherTrainer.setFirstName("Jane");
        anotherTrainer.setLastName("Smith");
        anotherTrainer.setUsername("Jane.Smith");
        anotherTrainer.setPassword("password01");
        anotherTrainer.setActive(true);

        assertEquals(trainer.hashCode(), anotherTrainer.hashCode());
    }

    @Test
    void testGetSpecialization() {
        TrainingType expectedSpecialization = new TrainingType();
        trainer.setSpecialization(expectedSpecialization);
        assertEquals(expectedSpecialization, trainer.getSpecialization());
    }

    @Test
    void testGetTrainees() {
        assertTrue(trainer.getTrainees().isEmpty());
    }

    @Test
    void testGetTrainings() {
        assertTrue(trainer.getTrainings().isEmpty());
    }

}