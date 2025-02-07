package micro.gymapp.unit.model;

import micro.gymapp.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTest {

    private Training training;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainee = Trainee.builder().build();
        trainee.setUserId(1l);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        trainer = Trainer.builder().build();
        trainer.setUserId(2l);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TrainingTypeEnum.FITNESS)
                .build();

        training = Training.builder()
                .id(1L)
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Morning Cardio")
                .trainingType(trainingType)
                .trainingDate(LocalDate.of(2023, 12, 25))
                .duration(60)
                .build();
    }

    @Test
    void testGetters() {
        assertEquals(1L, training.getId());
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals("Morning Cardio", training.getTrainingName());
        assertEquals(trainingType, training.getTrainingType());
        assertEquals(LocalDate.of(2023, 12, 25), training.getTrainingDate());
        assertEquals(60, training.getDuration());
    }

    @Test
    void testSetters() {
        Trainee newTrainee = Trainee.builder().build();
        trainee.setUserId(3l);
        trainee.setFirstName("Alice");
        trainee.setLastName("Johnson");

        training.setTrainee(newTrainee);
        assertEquals(newTrainee, training.getTrainee());

        training.setTrainingName("Evening Yoga");
        assertEquals("Evening Yoga", training.getTrainingName());
    }

    @Test
    void testEquals_SameObject() {
        assertTrue(training.equals(training));
    }

    @Test
    void testEquals_DifferentObject() {
        Training anotherTraining = Training.builder()
                .id(1L)
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Morning Cardio")
                .trainingType(trainingType)
                .trainingDate(LocalDate.of(2023, 12, 25))
                .duration(60)
                .build();

        assertTrue(training.equals(anotherTraining));
    }

    @Test
    void testEquals_DifferentId() {
        Training anotherTraining = Training.builder()
                .id(2L)
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Morning Cardio")
                .trainingType(trainingType)
                .trainingDate(LocalDate.of(2023, 12, 25))
                .duration(60)
                .build();

        assertFalse(training.equals(anotherTraining));
    }

    @Test
    void testHashCode() {
        Training anotherTraining = Training.builder()
                .id(1L)
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Morning Cardio")
                .trainingType(trainingType)
                .trainingDate(LocalDate.of(2023, 12, 25))
                .duration(60)
                .build();

        assertEquals(training.hashCode(), anotherTraining.hashCode());
    }
}
