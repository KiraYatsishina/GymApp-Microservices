package micro.gymapp.serviceTests;

import micro.gymapp.dto.CreateTrainingDTO;
import micro.gymapp.dto.TrainingDTO;
import micro.gymapp.model.*;
import micro.gymapp.repository.TraineeRepository;
import micro.gymapp.repository.TrainerRepository;
import micro.gymapp.repository.TrainingRepository;
import micro.gymapp.repository.TrainingTypeRepository;
import micro.gymapp.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByTraineeUsername_returnsTrainingDTOList() {
        String username = "traineeUser";
        LocalDate fromDate = LocalDate.now().minusDays(10);
        LocalDate toDate = LocalDate.now();
        String trainerName = "trainerUser";
        String trainingType = "FITNESS";

        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        Training training = new Training();
        training.setTrainingName("Morning fitness");
        training.setTrainer(trainer);
        TrainingType trainingTypeTraining = new TrainingType();
        trainingTypeTraining.setTrainingTypeName(TrainingTypeEnum.YOGA);
        training.setTrainingType(trainingTypeTraining);

        when(trainingRepository.findTraineeTrainings(username, fromDate, toDate, trainerName, trainingType))
                .thenReturn(Collections.singletonList(training));

        List<TrainingDTO> result = trainingService.findByTraineeUsername(username, fromDate, toDate, trainerName, trainingType);

        assertEquals(1, result.size());
        assertEquals("Morning fitness", result.get(0).getName());
        verify(trainingRepository, times(1)).findTraineeTrainings(username, fromDate, toDate, trainerName, trainingType);
    }

    @Test
    void testFindByTrainerUsername_returnsTrainingDTOList() {
        String username = "trainerUser";
        LocalDate fromDate = LocalDate.now().minusDays(10);
        LocalDate toDate = LocalDate.now();
        String traineeName = "traineeUser";

        Trainee trainee = new Trainee();
        trainee.setFirstName("Jane");
        trainee.setLastName("Smith");

        Training training = new Training();
        training.setTrainingName("Afternoon Strength");
        training.setTrainee(trainee);

        TrainingType trainingTypeTraining = new TrainingType();
        trainingTypeTraining.setTrainingTypeName(TrainingTypeEnum.YOGA);
        training.setTrainingType(trainingTypeTraining);

        when(trainingRepository.findTrainerTrainings(username, fromDate, toDate, traineeName))
                .thenReturn(Collections.singletonList(training));

        List<TrainingDTO> result = trainingService.findByTrainerUsername(username, fromDate, toDate, traineeName);

        assertEquals(1, result.size());
        assertEquals("Afternoon Strength", result.get(0).getName());
        verify(trainingRepository, times(1)).findTrainerTrainings(username, fromDate, toDate, traineeName);
    }

    @Test
    void testAddTraining_validRequest_returnsSavedTraining() throws Exception {
        CreateTrainingDTO request = new CreateTrainingDTO();
        request.setTraineeUsername("traineeUser");
        request.setTrainerUsername("trainerUser");
        request.setType("FITNESS");
        request.setName("Evening fitness");
        request.setDate(LocalDate.now());
        request.setDuration(60);

        Trainee trainee = new Trainee();
        trainee.setUsername("traineeUser");

        Trainer trainer = new Trainer();
        trainer.setUsername("trainerUser");

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeEnum.FITNESS);

        Training training = new Training();
        training.setTrainingName("Evening fitness");

        when(traineeRepository.findByUsername("traineeUser")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainerUser")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingTypeEnum.FITNESS)).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        Training result = trainingService.addTraining(request);

        assertNotNull(result);
        assertEquals("Evening fitness", result.getTrainingName());
        verify(traineeRepository, times(1)).findByUsername("traineeUser");
        verify(trainerRepository, times(1)).findByUsername("trainerUser");
        verify(trainingTypeRepository, times(1)).findByTrainingTypeName(TrainingTypeEnum.FITNESS);
        verify(trainingRepository, times(1)).save(any(Training.class));
    }

    @Test
    void testAddTraining_traineeNotFound_throwsException() {
        CreateTrainingDTO request = new CreateTrainingDTO();
        request.setTraineeUsername("nonExistentTrainee");

        when(traineeRepository.findByUsername("nonExistentTrainee")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> trainingService.addTraining(request));
        assertEquals("Trainee not found", exception.getMessage());
    }

    @Test
    void testAddTraining_trainerNotFound_throwsException() {
        CreateTrainingDTO request = new CreateTrainingDTO();
        request.setTraineeUsername("traineeUser");
        request.setTrainerUsername("nonExistentTrainer");

        when(traineeRepository.findByUsername("traineeUser")).thenReturn(Optional.of(new Trainee()));
        when(trainerRepository.findByUsername("nonExistentTrainer")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> trainingService.addTraining(request));
        assertEquals("Trainer not found", exception.getMessage());
    }

    @Test
    void testAddTraining_trainingTypeNotFound_throwsException() {
        CreateTrainingDTO request = new CreateTrainingDTO();
        request.setTraineeUsername("traineeUser");
        request.setTrainerUsername("trainerUser");
        request.setType("NON_EXISTENT_TYPE");

        when(traineeRepository.findByUsername("traineeUser")).thenReturn(Optional.of(new Trainee()));
        when(trainerRepository.findByUsername("trainerUser")).thenReturn(Optional.of(new Trainer()));
        when(trainingTypeRepository.findByTrainingTypeName(any(TrainingTypeEnum.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> trainingService.addTraining(request));
        assertEquals("Training type not found", exception.getMessage());
    }

    @Test
    void testAddTraining_trainingTypeNotFoundInRepository_throwsException() {
        CreateTrainingDTO request = new CreateTrainingDTO();
        request.setTraineeUsername("traineeUser");
        request.setTrainerUsername("trainerUser");
        request.setType("FITNESS");

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        when(traineeRepository.findByUsername("traineeUser")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainerUser")).thenReturn(Optional.of(trainer));

        TrainingTypeEnum trainingTypeEnum = TrainingTypeEnum.valueOf(request.getType());
        when(trainingTypeRepository.findByTrainingTypeName(trainingTypeEnum)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> trainingService.addTraining(request));
        assertEquals("Training type not found", exception.getMessage());

        verify(trainingTypeRepository, times(1)).findByTrainingTypeName(trainingTypeEnum);
    }
}