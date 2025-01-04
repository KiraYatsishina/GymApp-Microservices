package micro.gymapp.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import micro.gymapp.client.TrainersWorkloadClient;
import micro.gymapp.dto.ActionTrainingDTO;
import micro.gymapp.dto.CreateTrainingDTO;
import micro.gymapp.dto.Trainer.SignupTrainer;
import micro.gymapp.dto.Trainer.TrainerDTO;
import micro.gymapp.dto.Trainer.UpdateTrainerDTO;
import micro.gymapp.dto.TrainingDTO;
import micro.gymapp.dto.UserDTO;
import micro.gymapp.mapper.TrainerMapper;
import micro.gymapp.mapper.TrainingMapper;
import micro.gymapp.model.Trainer;
import micro.gymapp.model.Training;
import micro.gymapp.model.TrainingTypeEnum;
import micro.gymapp.repository.TrainerRepository;
import micro.gymapp.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import micro.gymapp.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
@Tag(name = "Trainer Controller", description = "Endpoints for trainer profile management")
public class TrainerController {

    private static final Logger logger = LoggerFactory.getLogger(TrainerController.class);

    private final TrainerService trainerService;
    private final Timer trainerTrainingListTimer;
    private final TrainingService trainingService;
    private final TrainersWorkloadClient trainersWorkloadClient;
    private final TrainerRepository trainerRepository;

    @Autowired
    public TrainerController(TrainerService trainerService,
                             MeterRegistry meterRegistry,
                             TrainingService trainingService,
                             TrainersWorkloadClient trainersWorkloadClient, TrainerRepository trainerRepository) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainerTrainingListTimer = meterRegistry.timer("trainer.trainingList.execution.time");
        this.trainersWorkloadClient = trainersWorkloadClient;
        this.trainerRepository = trainerRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupTrainer trainerDTO) {

        if (trainerDTO.getFirstName() == null || trainerDTO.getFirstName().length() < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("First name length must be at least 1 character");
        }
        if (trainerDTO.getLastName() == null || trainerDTO.getLastName().length() < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Last name length must be at least 1 character");
        }

        try {
            TrainingTypeEnum.valueOf(trainerDTO.getSpecialization());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid specialization");
        }

        Trainer trainer = trainerService.mapToEntity(trainerDTO);
        Optional<UserDTO> createdUser = trainerService.signUpTrainer(trainer);
        if(createdUser.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
    }

    @GetMapping("/myProfile")
    @Operation(summary = "Retrieve trainer profile", description = "Fetches the profile information for the authenticated trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TrainerDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trainer profile not found", content = @Content)
    })
    public ResponseEntity<TrainerDTO> getMyProfile(Principal principal) {
        String transactionId = UUID.randomUUID().toString();
        String username = principal.getName();
        logger.info("Transaction ID: {}, Endpoint: /myProfile, Request received for user: {}", transactionId, username);
        Optional<TrainerDTO> trainerDTOOptional = trainerService.findByUsername(username);

        if (trainerDTOOptional.isPresent()) {
            logger.info("Transaction ID: {}, Trainer profile found for user: {}", transactionId, username);
            return ResponseEntity.ok(trainerDTOOptional.get());
        } else {
            logger.warn("Transaction ID: {}, Trainer profile not found for user: {}", transactionId, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/updateProfile")
    @Operation(summary = "Update trainer profile", description = "Updates the profile information of the authenticated trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = TrainerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    public ResponseEntity<?> updateTrainerProfile(Principal principal, @RequestBody UpdateTrainerDTO updateTrainerDTO) {
        String transactionId = UUID.randomUUID().toString();
        String username = principal.getName();
        logger.info("Transaction ID: {}, Endpoint: /updateProfile, Request received for user: {}", transactionId, username);
        if (updateTrainerDTO.getFirstName() == null || updateTrainerDTO.getFirstName().isEmpty()) {
            logger.warn("Transaction ID: {}, First name is missing", transactionId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("First Name is required");
        }
        if (updateTrainerDTO.getLastName() == null || updateTrainerDTO.getLastName().isEmpty()) {
            logger.warn("Transaction ID: {}, Last name is missing", transactionId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Last Name is required");
        }
        if (updateTrainerDTO.getSpecialization() == null || updateTrainerDTO.getSpecialization().isEmpty()) {
            logger.warn("Transaction ID: {}, Specialization is missing", transactionId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Specialization is required");
        }

        Optional<Trainer> updatedTrainer = trainerService.updateTrainerProfile(username, updateTrainerDTO);
        if (updatedTrainer.isPresent()) {
            TrainerDTO trainerDTO = TrainerMapper.toDTO(updatedTrainer.get(), true);
            logger.info("Transaction ID: {}, Trainer profile updated successfully for user: {}", transactionId, username);
            return ResponseEntity.ok(trainerDTO);
        }
        logger.error("Transaction ID: {}, Failed to update profile for user: {}", transactionId, username);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something with request");
    }

    @GetMapping("/trainingList")
    @Operation(summary = "Get trainer training list", description = "Retrieve list of trainings for the authenticated trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training list retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<?> getTrainerTrainingList(Principal principal,
                                                    @Parameter(description = "Start date to filter trainings")
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                    @Parameter(description = "End date to filter trainings")
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                    @Parameter(description = "Trainee's name to filter trainings")
                                                    @RequestParam(required = false) String traineeName) {
        return trainerTrainingListTimer.record(() -> {
            String transactionId = UUID.randomUUID().toString();
            String username = principal.getName();
            logger.info("Transaction ID: {}, Endpoint: /trainer/trainingList, Request received for trainer: {}", transactionId, username);
            List<TrainingDTO> trainings = trainingService.findByTrainerUsername(username, fromDate, toDate, traineeName);
            logger.info("Transaction ID: {}, Training list retrieved successfully for trainer: {}", transactionId, username);
            return new ResponseEntity<>(trainings, HttpStatus.OK);
        });
    }


    @PostMapping("/addTraining")
    @Operation(summary = "Add training", description = "Allows a trainer to add a new training session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training added successfully",
                    content = @Content(schema = @Schema(implementation = TrainingDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<?> addTraining(@RequestBody CreateTrainingDTO createTrainingDTO) {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Transaction ID: {}, Endpoint: /trainer/addTraining, Request received to add training", transactionId);

        try {
            Trainer trainer = trainerRepository.findByUsername(createTrainingDTO.getTrainerUsername()).get();
            ActionTrainingDTO actionTrainingDTO = ActionTrainingDTO.builder()
                    .userName(createTrainingDTO.getTrainerUsername())
                    .firstName(trainer.getFirstName())
                    .lastName(trainer.getLastName())
                    .isActive(true)
                    .trainingDate(createTrainingDTO.getDate().toString())
                    .duration(createTrainingDTO.getDuration())
                    .actionType("ADD")
                    .build();

            ResponseEntity<?> response = trainersWorkloadClient.updateTrainerWorkload(actionTrainingDTO);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("Failed to update trainer workload");
            }

            Training training = trainingService.addTraining(createTrainingDTO);
            logger.info("Transaction ID: {}, Training added successfully for trainer: {}", transactionId, createTrainingDTO.getTrainerUsername());

            return ResponseEntity.ok(TrainingMapper.toDTO(training, false));
        } catch (Exception e) {
            logger.error("Transaction ID: {}, Failed to add training, Error: {}", transactionId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/deleteTraining")
    @Operation(summary = "Delete training", description = "Allows a trainer to delete an existing training session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Training not found", content = @Content)
    })
    public ResponseEntity<?> deleteTraining(Principal principal, @RequestBody Long trainingId) {
        String transactionId = UUID.randomUUID().toString();
        String username = principal.getName();
        logger.info("Transaction ID: {}, Endpoint: /trainer/deleteTraining, Request received to delete training for trainer: {}", transactionId, username);

        try {
            Optional<Training> trainingOpt = trainingService.getTrainingById(trainingId);

            if (trainingOpt.isEmpty()) {
                logger.warn("Transaction ID: {}, Training with ID {} not found for trainer: {}", transactionId, trainingId, username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Training not found");
            }

            Training training = trainingOpt.get();
            ActionTrainingDTO actionTrainingDTO = ActionTrainingDTO.builder()
                    .userName(training.getTrainer().getUsername())
                    .firstName(training.getTrainer().getFirstName())
                    .lastName(training.getTrainer().getLastName())
                    .isActive(training.getTrainer().isActive())
                    .trainingDate(training.getTrainingDate().toString())
                    .duration(training.getDuration())
                    .actionType("DELETE")
                    .build();


            ResponseEntity<?> response = trainersWorkloadClient.updateTrainerWorkload(actionTrainingDTO);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("Failed to update trainer workload");
            }

            Training deletedTraining = trainingService.deleteTraining(trainingId);
            logger.info("Transaction ID: {}, Training deleted successfully for trainer: {}", transactionId, username);

            return ResponseEntity.ok(TrainingMapper.toDTO(deletedTraining, false));
        } catch (Exception e) {
            logger.error("Transaction ID: {}, Failed to delete training for trainer: {}, Error: {}", transactionId, username, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
