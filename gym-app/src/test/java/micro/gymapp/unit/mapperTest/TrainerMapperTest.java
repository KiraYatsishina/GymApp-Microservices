package micro.gymapp.unit.mapperTest;

import micro.gymapp.dto.Trainer.ShortTrainerDTO;
import micro.gymapp.dto.Trainer.TrainerDTO;
import micro.gymapp.mapper.TrainerMapper;
import micro.gymapp.model.Trainee;
import micro.gymapp.model.Trainer;
import micro.gymapp.model.TrainingType;
import micro.gymapp.model.TrainingTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {

    private Trainer trainer;
    private TrainingType trainingType;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeEnum.YOGA);

        trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername(trainee.getFirstName() + "." + trainee.getLastName());

        trainer = new Trainer();
        trainer.setUsername("trainer.test");
        trainer.setFirstName("TrainerFirst");
        trainer.setLastName("TrainerLast");
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(Collections.singletonList(trainee));
    }

    @Test
    void testToDTO_WithTraineeList() {
        TrainerDTO trainerDTO = TrainerMapper.toDTO(trainer, true);

        assertEquals("trainer.test", trainerDTO.getUsername());
        assertEquals("TrainerFirst", trainerDTO.getFirstName());
        assertEquals("TrainerLast", trainerDTO.getLastName());
        assertTrue(trainerDTO.isActive());
        assertEquals("YOGA", trainerDTO.getSpecialization());  // Check specialization
        assertNotNull(trainerDTO.getTrainees());
        assertEquals(1, trainerDTO.getTrainees().size());
        assertEquals("John.Doe", trainerDTO.getTrainees().get(0).getUsername());
    }

    @Test
    void testToDTO_WithoutTraineeList() {
        TrainerDTO trainerDTO = TrainerMapper.toDTO(trainer, false);

        assertEquals("trainer.test", trainerDTO.getUsername());
        assertEquals("TrainerFirst", trainerDTO.getFirstName());
        assertEquals("TrainerLast", trainerDTO.getLastName());
        assertTrue(trainerDTO.isActive());
        assertEquals("YOGA", trainerDTO.getSpecialization());  // Check specialization
        assertNull(trainerDTO.getTrainees());  // No trainee list
    }

    @Test
    void testToShortDTO() {
        ShortTrainerDTO shortTrainerDTO = TrainerMapper.toShortDTO(trainer);

        assertEquals("trainer.test", shortTrainerDTO.getUsername());
        assertEquals("TrainerFirst", shortTrainerDTO.getFirstName());
        assertEquals("TrainerLast", shortTrainerDTO.getLastName());
        assertEquals("YOGA", shortTrainerDTO.getSpecialization());
    }

}
