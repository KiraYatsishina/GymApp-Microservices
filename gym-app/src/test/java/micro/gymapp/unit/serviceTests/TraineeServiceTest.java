package micro.gymapp.unit.serviceTests;

import micro.gymapp.dto.Trainee.SignupTrainee;
import micro.gymapp.dto.Trainee.TraineeDTO;
import micro.gymapp.dto.Trainee.UpdateTraineeDTO;
import micro.gymapp.dto.Trainer.ShortTrainerDTO;
import micro.gymapp.model.Trainee;
import micro.gymapp.model.Trainer;
import micro.gymapp.model.TrainingType;
import micro.gymapp.model.TrainingTypeEnum;
import micro.gymapp.repository.TraineeRepository;
import micro.gymapp.repository.TrainerRepository;
import micro.gymapp.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    @InjectMocks
    private TraineeService traineeService;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByUsernameSuccess() {
        String username = "testUser";
        Trainee trainee = new Trainee();
        trainee.setUsername(username);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        Optional<TraineeDTO> result = traineeService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    @Test
    void testMapToEntitySuccess() {
        SignupTrainee signupTrainee = new SignupTrainee();
        signupTrainee.setFirstName("John");
        signupTrainee.setLastName("Doe");

        Trainee result = traineeService.mapToEntity(signupTrainee);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void testMapToEntityWithNull() {
        Trainee result = traineeService.mapToEntity(null);
        assertNull(result);
    }

    @Test
    void testUpdateTraineeProfileSuccess() {
        String username = "testUser";
        UpdateTraineeDTO updateTraineeDTO = new UpdateTraineeDTO();
        updateTraineeDTO.setUsername(username);
        updateTraineeDTO.setFirstName("UpdatedFirstName");

        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setFirstName("OldFirstName");

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        Optional<Trainee> result = traineeService.updateTraineeProfile(updateTraineeDTO);

        assertTrue(result.isPresent());
        assertEquals("UpdatedFirstName", result.get().getFirstName());
    }

    @Test
    void testUpdateTraineeProfileNotFound() {
        String username = "nonexistentUser";
        UpdateTraineeDTO updateTraineeDTO = new UpdateTraineeDTO();

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.updateTraineeProfile(updateTraineeDTO);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetNotAssignedTrainersList() {
        String username = "testUser";
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeEnum.YOGA);
        trainer.setSpecialization(trainingType);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeRepository.findNotAssignedTrainers(1L)).thenReturn(Collections.singletonList(trainer));

        List<ShortTrainerDTO> result = traineeService.getNotAssignedTrainersList(username);

        assertEquals(1, result.size());
        assertEquals("trainer1", result.get(0).getUsername());
    }

    @Test
    void testUpdateTraineeTrainers() {
        String traineeUsername = "testUser";
        List<String> trainerUsernames = List.of("trainer1", "trainer2");

        Trainee trainee = new Trainee();
        Trainer trainer1 = new Trainer();
        trainer1.setUsername("trainer1");
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeEnum.ZUMBA);
        trainer1.setSpecialization(trainingType);
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("trainer2");
        trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeEnum.YOGA);
        trainer2.setSpecialization(trainingType);

        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsernameIn(trainerUsernames)).thenReturn(List.of(trainer1, trainer2));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        List<ShortTrainerDTO> result = traineeService.updateTraineeTrainers(traineeUsername, trainerUsernames);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getUsername().equals("trainer1")));
        assertTrue(result.stream().anyMatch(dto -> dto.getUsername().equals("trainer2")));
    }

    @Test
    void testCountActiveTrainees() {
        long activeTraineesCount = 5L;
        when(traineeRepository.countByIsActive(true)).thenReturn(activeTraineesCount);

        long result = traineeService.countActiveTrainees();

        assertEquals(activeTraineesCount, result);
        verify(traineeRepository, times(1)).countByIsActive(true);
    }
}