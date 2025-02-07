package micro.gymapp.unit.model;

import micro.gymapp.model.Trainer;
import micro.gymapp.model.Training;
import micro.gymapp.model.TrainingType;
import micro.gymapp.model.TrainingTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeTest {

    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TrainingTypeEnum.FITNESS)
                .trainings(new ArrayList<>())
                .trainers(new ArrayList<>())
                .build();
    }

    @Test
    void testGetters() {
        assertEquals(1L, trainingType.getId());
        assertEquals(TrainingTypeEnum.FITNESS, trainingType.getTrainingTypeName());
        assertTrue(trainingType.getTrainings().isEmpty());
        assertTrue(trainingType.getTrainers().isEmpty());
    }

    @Test
    void testSetters() {
        trainingType.setId(2L);
        assertEquals(2L, trainingType.getId());

        trainingType.setTrainingTypeName(TrainingTypeEnum.STRETCHING);
        assertEquals(TrainingTypeEnum.STRETCHING, trainingType.getTrainingTypeName());

        List<Training> trainingList = new ArrayList<>();
        trainingList.add(new Training());
        trainingType.setTrainings(trainingList);
        assertEquals(1, trainingType.getTrainings().size());

        List<Trainer> trainerList = new ArrayList<>();
        trainerList.add(new Trainer());
        trainingType.setTrainers(trainerList);
        assertEquals(1, trainingType.getTrainers().size());
    }

    @Test
    void testEquals_SameObject() {
        assertTrue(trainingType.equals(trainingType));
    }

    @Test
    void testEquals_DifferentObject() {
        TrainingType anotherTrainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TrainingTypeEnum.FITNESS)
                .build();

        assertTrue(trainingType.equals(anotherTrainingType));
    }

    @Test
    void testEquals_DifferentId() {
        TrainingType anotherTrainingType = TrainingType.builder()
                .id(2L)
                .trainingTypeName(TrainingTypeEnum.FITNESS)
                .build();

        assertFalse(trainingType.equals(anotherTrainingType));
    }

    @Test
    void testHashCode() {
        TrainingType anotherTrainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TrainingTypeEnum.FITNESS)
                .build();

        assertEquals(trainingType.hashCode(), anotherTrainingType.hashCode());
    }

    @Test
    void testToString() {
        String toString = trainingType.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("trainingTypeName=FITNESS"));
    }
}
