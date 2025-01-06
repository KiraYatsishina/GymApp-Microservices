package micro.gymapp.serviceTests;

import micro.gymapp.dto.Trainer.SignupTrainer;
import micro.gymapp.dto.Trainer.TrainerDTO;
import micro.gymapp.dto.Trainer.UpdateTrainerDTO;
import micro.gymapp.model.Trainer;
import micro.gymapp.model.TrainingType;
import micro.gymapp.model.TrainingTypeEnum;
import micro.gymapp.repository.TrainerRepository;
import micro.gymapp.repository.TrainingTypeRepository;
import micro.gymapp.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByUsername_existingUser_returnsTrainerDTO() {
        String username = "testUser";
        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeEnum.YOGA);
        trainer.setSpecialization(trainingType);

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        Optional<TrainerDTO> result = trainerService.findByUsername(username);

        assertTrue(result.isPresent());
        verify(trainerRepository, times(1)).findByUsername(username);
    }

    @Test
    void testMapToEntityWithNull() {
        Trainer result = trainerService.mapToEntity(null);
        assertNull(result);
    }


    @Test
    void testFindByUsername_nonExistentUser_returnsEmptyOptional() {
        String username = "unknownUser";

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<TrainerDTO> result = trainerService.findByUsername(username);

        assertFalse(result.isPresent());
        verify(trainerRepository, times(1)).findByUsername(username);
    }

    @Test
    void testMapToEntity_validSignupTrainer_returnsTrainer() {
        SignupTrainer signupTrainer = new SignupTrainer();
        signupTrainer.setFirstName("John");
        signupTrainer.setLastName("Doe");
        signupTrainer.setSpecialization("ZUMBA");

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeEnum.ZUMBA);

        when(trainingTypeRepository.findByTrainingTypeName(TrainingTypeEnum.ZUMBA))
                .thenReturn(Optional.of(trainingType));

        Trainer result = trainerService.mapToEntity(signupTrainer);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(trainingType, result.getSpecialization());
    }

    @Test
    void testMapToEntity_invalidSpecialization_throwsRuntimeException() {
        SignupTrainer signupTrainer = new SignupTrainer();
        signupTrainer.setFirstName("John");
        signupTrainer.setLastName("Doe");
        signupTrainer.setSpecialization("INVALID");

        assertThrows(RuntimeException.class, () -> trainerService.mapToEntity(signupTrainer));
    }

    @Test
    void testUpdateTrainerProfile_existingTrainer_updatesTrainer() {
        String username = "testUser";
        UpdateTrainerDTO updateTrainerDTO = new UpdateTrainerDTO();
        updateTrainerDTO.setFirstName("Jane");
        updateTrainerDTO.setLastName("Smith");
        updateTrainerDTO.setSpecialization("ZUMBA");
        updateTrainerDTO.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUsername(username);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeEnum.ZUMBA);

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingTypeEnum.ZUMBA))
                .thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        Optional<Trainer> result = trainerService.updateTrainerProfile(username, updateTrainerDTO);

        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
        assertEquals("Smith", result.get().getLastName());
        assertEquals(trainingType, result.get().getSpecialization());
        assertTrue(result.get().isActive());
    }

    @Test
    void testUpdateTrainerProfile_invalidSpecialization_returnsEmptyOptional() {
        String username = "testUser";
        UpdateTrainerDTO updateTrainerDTO = new UpdateTrainerDTO();
        updateTrainerDTO.setSpecialization("INVALID");

        Trainer trainer = new Trainer();
        trainer.setUsername(username);

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.updateTrainerProfile(username, updateTrainerDTO);

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateTrainerProfile_nonExistentTrainer_returnsEmptyOptional() {
        String username = "unknownUser";
        UpdateTrainerDTO updateTrainerDTO = new UpdateTrainerDTO();

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.updateTrainerProfile(username, updateTrainerDTO);

        assertFalse(result.isPresent());
    }
}