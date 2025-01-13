package micro.gymapp.controllerTests;

import io.micrometer.core.instrument.MeterRegistry;
import micro.gymapp.controller.TrainerController;
import micro.gymapp.dto.Trainer.SignupTrainer;
import micro.gymapp.dto.Trainer.TrainerDTO;
import micro.gymapp.dto.Trainer.UpdateTrainerDTO;
import micro.gymapp.dto.UserDTO;
import micro.gymapp.model.Trainer;
import micro.gymapp.model.TrainingType;
import micro.gymapp.model.TrainingTypeEnum;
import micro.gymapp.service.TrainerService;
import micro.gymapp.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private TrainerController trainerController;

    private Principal mockPrincipal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("testTrainer");

        when(meterRegistry.timer("trainer.trainingList.execution.time"))
                .thenReturn(mock(io.micrometer.core.instrument.Timer.class));
    }

    @Test
    void testSignupSuccess() {
        SignupTrainer trainerDTO = new SignupTrainer("John", "Doe", "FITNESS");
        Trainer trainer = new Trainer();
        Optional<UserDTO> createdUser = Optional.of(new UserDTO("John", "Doe"));

        when(trainerService.mapToEntity(trainerDTO)).thenReturn(trainer);
        when(trainerService.signUpTrainer(trainer)).thenReturn(createdUser);

        ResponseEntity<?> response = trainerController.signup(trainerDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdUser, response.getBody());
    }

    @Test
    void testSignupInvalidSpecialization() {
        SignupTrainer trainerDTO = new SignupTrainer("John", "Doe", "INVALID");

        ResponseEntity<?> response = trainerController.signup(trainerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid specialization", response.getBody());
    }

    @Test
    void testDeleteTrainingNotFound() {
        Long trainingId = 1L;

        when(trainingService.getTrainingById(trainingId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = trainerController.deleteTraining(mockPrincipal, trainingId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Training not found", response.getBody());
    }

    @Test
    void testGetMyProfile_Success() {
        TrainerDTO mockTrainerDTO = new TrainerDTO();
        when(trainerService.findByUsername("testTrainer")).thenReturn(Optional.of(mockTrainerDTO));

        ResponseEntity<TrainerDTO> response = trainerController.getMyProfile(mockPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTrainerDTO, response.getBody());
        verify(trainerService, times(1)).findByUsername("testTrainer");
    }

    @Test
    void testGetMyProfile_NotFound() {
        when(trainerService.findByUsername("testTrainer")).thenReturn(Optional.empty());

        ResponseEntity<TrainerDTO> response = trainerController.getMyProfile(mockPrincipal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(trainerService, times(1)).findByUsername("testTrainer");
    }

    @Test
    void testUpdateTrainerProfile_Success() {
        UpdateTrainerDTO updateTrainerDTO = new UpdateTrainerDTO();
        updateTrainerDTO.setFirstName("Jane");
        updateTrainerDTO.setLastName("Doe");
        updateTrainerDTO.setSpecialization("Yoga");

        Trainer mockTrainer = new Trainer();
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeEnum.YOGA);
        mockTrainer.setSpecialization(trainingType);

        mockTrainer.setUsername("testTrainer");

        when(trainerService.updateTrainerProfile("testTrainer", updateTrainerDTO)).thenReturn(Optional.of(mockTrainer));

        ResponseEntity<?> response = trainerController.updateTrainerProfile(mockPrincipal, updateTrainerDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(trainerService, times(1)).updateTrainerProfile("testTrainer", updateTrainerDTO);
    }

    @Test
    void testUpdateTrainerProfile_MissingFirstName() {
        UpdateTrainerDTO updateTrainerDTO = new UpdateTrainerDTO();
        updateTrainerDTO.setLastName("Doe");
        updateTrainerDTO.setSpecialization("Yoga");

        ResponseEntity<?> response = trainerController.updateTrainerProfile(mockPrincipal, updateTrainerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("First Name is required", response.getBody());
    }

    @Test
    void testUpdateTrainerProfile_MissingLastName() {
        UpdateTrainerDTO updateTrainerDTO = new UpdateTrainerDTO();
        updateTrainerDTO.setFirstName("Jane");
        updateTrainerDTO.setSpecialization("Yoga");

        ResponseEntity<?> response = trainerController.updateTrainerProfile(mockPrincipal, updateTrainerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Last Name is required", response.getBody());
    }

    @Test
    void testUpdateTrainerProfile_MissingSpecialization() {
        UpdateTrainerDTO updateTrainerDTO = new UpdateTrainerDTO();
        updateTrainerDTO.setFirstName("Jane");
        updateTrainerDTO.setLastName("Doe");

        ResponseEntity<?> response = trainerController.updateTrainerProfile(mockPrincipal, updateTrainerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Specialization is required", response.getBody());
    }

    @Test
    void testUpdateTrainerProfile_Failure() {
        UpdateTrainerDTO updateTrainerDTO = new UpdateTrainerDTO();
        updateTrainerDTO.setFirstName("Jane");
        updateTrainerDTO.setLastName("Doe");
        updateTrainerDTO.setSpecialization("Yoga");

        when(trainerService.updateTrainerProfile("testTrainer", updateTrainerDTO)).thenReturn(Optional.empty());

        ResponseEntity<?> response = trainerController.updateTrainerProfile(mockPrincipal, updateTrainerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Something with request", response.getBody());
        verify(trainerService, times(1)).updateTrainerProfile("testTrainer", updateTrainerDTO);
    }
}