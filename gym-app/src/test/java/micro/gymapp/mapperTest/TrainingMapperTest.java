package micro.gymapp.mapperTest;

import micro.gymapp.dto.TrainingDTO;
import micro.gymapp.mapper.TrainingMapper;
import micro.gymapp.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingMapperTest {

    private Training training;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;

    private LocalDate trainingDate = LocalDate.of(2024, 10, 23);

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeEnum.YOGA);

        trainer = new Trainer();
        trainer.setFirstName("TrainerFirst");
        trainer.setLastName("TrainerLast");
        trainer.setUsername("trainer.test");

        trainee = new Trainee();
        trainee.setFirstName("TraineeFirst");
        trainee.setLastName("TraineeLast");
        trainee.setUsername("trainee.test");

        training = new Training();
        training.setTrainingName("Yoga Session");
        training.setTrainingDate(trainingDate);
        training.setTrainingType(trainingType);
        training.setDuration(60);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
    }

    @Test
    void testToDTO_AsTrainee() {
        TrainingDTO trainingDTO = TrainingMapper.toDTO(training, true);

        assertEquals("Yoga Session", trainingDTO.getName());
        assertEquals(trainingDate, trainingDTO.getDate());
        assertEquals("YOGA", trainingDTO.getType());
        assertEquals(60, trainingDTO.getDuration());
        assertEquals("TrainerFirst TrainerLast", trainingDTO.getPersonName());
    }

    @Test
    void testToDTO_AsTrainer() {
        TrainingDTO trainingDTO = TrainingMapper.toDTO(training, false);

        assertEquals("Yoga Session", trainingDTO.getName());
        assertEquals(trainingDate, trainingDTO.getDate());
        assertEquals("YOGA", trainingDTO.getType());
        assertEquals(60, trainingDTO.getDuration());
        assertEquals("TraineeFirst TraineeLast", trainingDTO.getPersonName());
    }
}