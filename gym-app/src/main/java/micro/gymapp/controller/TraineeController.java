package micro.gymapp.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import micro.gymapp.dto.Trainee.SignupTrainee;
import micro.gymapp.dto.Trainee.TraineeDTO;
import micro.gymapp.dto.Trainee.UpdateTraineeDTO;
import micro.gymapp.dto.Trainee.UpdateTrainersListForm;
import micro.gymapp.dto.Trainer.ShortTrainerDTO;
import micro.gymapp.dto.TrainingDTO;
import micro.gymapp.dto.UserDTO;
import micro.gymapp.mapper.TraineeMapper;
import micro.gymapp.model.Trainee;
import micro.gymapp.model.User;
import micro.gymapp.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import micro.gymapp.service.TrainingService;
import micro.gymapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trainee")
@RequiredArgsConstructor
@Tag(name = "Trainee Controller", description = "Endpoints for trainee profile management")
public class TraineeController {

    private static final Logger logger = LoggerFactory.getLogger(TraineeController.class);

    private final TraineeService traineeService;
    private final UserService userService;
    private final Timer traineeTrainingListTimer;
    private final TrainingService trainingService;

    @Autowired
    public TraineeController(TraineeService traineeService,
                             UserService userService,
                             MeterRegistry meterRegistry,
                             TrainingService trainingService) {
        this.traineeService = traineeService;
        this.userService = userService;
        this.trainingService = trainingService;
        this.traineeTrainingListTimer = meterRegistry.timer("trainee.trainingList.execution.time");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupTrainee traineeDTO) {
        if (traineeDTO.getFirstName() == null || traineeDTO.getFirstName().length() < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("First name length must be at least 1 character");
        }
        if (traineeDTO.getLastName() == null || traineeDTO.getLastName().length() < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Last name length must be at least 1 character");
        }
        Trainee trainee = traineeService.mapToEntity(traineeDTO);
        Optional<UserDTO> createdUser = traineeService.signUpTrainee(trainee);
        if (createdUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
    }

    @GetMapping("/myProfile")
    @Operation(summary = "Retrieve trainee profile", description = "Fetches the profile information for the authenticated trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TraineeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trainee profile not found", content = @Content)
    })
    public ResponseEntity<TraineeDTO> getMyProfile(
            @RequestParam String username) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Transaction ID: {}, Endpoint: /myProfile, Request received for user: {}", transactionId, username);
        Optional<TraineeDTO> traineeDTOOptional = traineeService.findByUsername(username);

        if (traineeDTOOptional.isPresent()) {
            logger.info("Transaction ID: {}, Trainee profile found for user: {}", transactionId, username);
            return ResponseEntity.ok(traineeDTOOptional.get());
        } else {
            logger.warn("Transaction ID: {}, Trainee profile not found for user: {}", transactionId, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/updateProfile")
    @Operation(summary = "Update trainee profile", description = "Updates the profile information of the authenticated trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = TraineeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    public ResponseEntity<?> updateTraineeProfile(@RequestBody UpdateTraineeDTO updateTraineeDTO) {
        String transactionId = UUID.randomUUID().toString();
        String username = updateTraineeDTO.getUsername();
        logger.info("Transaction ID: {}, Endpoint: /updateProfile, Request received for user: {}", transactionId, username);

        if (updateTraineeDTO.getFirstName() == null || updateTraineeDTO.getFirstName().isEmpty()) {
            logger.warn("Transaction ID: {}, First name is missing", transactionId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("First Name is required");
        }
        if (updateTraineeDTO.getLastName() == null || updateTraineeDTO.getLastName().isEmpty()) {
            logger.warn("Transaction ID: {}, Last name is missing", transactionId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Last Name is required");
        }

        Optional<Trainee> updatedTrainee = traineeService.updateTraineeProfile(updateTraineeDTO);
        if (updatedTrainee.isPresent()) {
            TraineeDTO traineeDTO = TraineeMapper.toDTO(updatedTrainee.get(), true);
            logger.info("Transaction ID: {}, Profile updated successfully for user: {}", transactionId, username);
            return ResponseEntity.ok(traineeDTO);
        }

        logger.warn("Transaction ID: {}, Trainee not found for user: {}", transactionId, username);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found");
    }


    @GetMapping("/notAssignedTrainersList")
    @Operation(summary = "Get not assigned trainers list", description = "Retrieves a list of trainers not assigned to the trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unassigned trainers list retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShortTrainerDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    public ResponseEntity<List<ShortTrainerDTO>> getNotAssignedTrainersList(@RequestParam String username) {
        String transactionId = UUID.randomUUID().toString();

        logger.info("Transaction ID: {}, Endpoint: /notAssignedTrainersList, Request received for Trainee: {}", transactionId, username);
        Optional<TraineeDTO> traineeDTOOptional = traineeService.findByUsername(username);
        if(!traineeDTOOptional.isPresent()) {
            logger.warn("Transaction ID: {}, Trainee not found for /notAssignedTrainersList request", transactionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<ShortTrainerDTO> unassignedTrainers = traineeService.getNotAssignedTrainersList(username);
        logger.info("Transaction ID: {}, Unassigned trainers list retrieved for Trainee: {}", transactionId, username);
        return ResponseEntity.ok(unassignedTrainers);
    }

    @PutMapping("/updateTrainersList")
    @Operation(summary = "Update trainee's trainers list", description = "Updates the list of trainers assigned to the trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers list updated successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShortTrainerDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid trainer usernames", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    public ResponseEntity<?> updateTrainersList(@RequestBody UpdateTrainersListForm updateTrainersListForm) {
        String transactionId = UUID.randomUUID().toString();
        String username = updateTrainersListForm.getUsername();
        logger.info("Transaction ID: {}, Endpoint: /updateTrainersList, Request received for user: {}", transactionId, username);
        List<ShortTrainerDTO> updatedTrainers = traineeService.updateTraineeTrainers(username, updateTrainersListForm.getTrainerUsernames());
        logger.info("Transaction ID: {}, Trainers list updated successfully for user: {}", transactionId, username);
        return ResponseEntity.ok(updatedTrainers);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete user profile", description = "Deletes the profile of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile deleted successfully.", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content)
    })
    public ResponseEntity<?> deleteUser(@RequestParam String username) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Transaction ID: {}, Request to delete user: {}", transactionId, username);

        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            logger.warn("Transaction ID: {}, User {} not found.", transactionId, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        boolean isDeleted = userService.deleteUserByUsername(username);

        if (isDeleted) {
            logger.info("Transaction ID: {}, User {} deleted successfully.", transactionId, username);
            return ResponseEntity.ok("User profile deleted successfully.");
        } else {
            logger.warn("Transaction ID: {}, User {} not found during deletion.", transactionId, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @GetMapping("/trainingList")
    @Operation(summary = "Get trainee training list", description = "Retrieve list of trainings for the authenticated trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training list retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    public ResponseEntity<List<TrainingDTO> > getTraineeTrainingList(@RequestParam String username,
                                                    @Parameter(description = "Start date to filter trainings")
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                    @Parameter(description = "End date to filter trainings")
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                    @Parameter(description = "Trainer's name to filter trainings")
                                                    @RequestParam(required = false) String trainerName,
                                                    @Parameter(description = "Training type to filter trainings")
                                                    @RequestParam(required = false) String trainingType) {
        return traineeTrainingListTimer.record(() -> {
            String transactionId = UUID.randomUUID().toString();
            logger.info("Transaction ID: {}, Endpoint: /trainee/trainingList, Request received for trainee: {}", transactionId, username);
            List<TrainingDTO> trainings = trainingService.findByTraineeUsername(username, fromDate, toDate, trainerName, trainingType);
            logger.info("Transaction ID: {}, Training list retrieved successfully for trainee: {}", transactionId, username);
            return new ResponseEntity<>(trainings, HttpStatus.OK);
        });
    }
}
