package micro.gymapp.controller;

import micro.gymapp.dto.TrainingTypeDTO;
import micro.gymapp.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Training type Controller")
public class TrainingTypeController {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeController.class);

    private final TrainingTypeService trainingTypeService;

    @GetMapping("/allTrainingTypes")
    @Operation(summary = "Retrieve all training types", description = "Gets a list of all available training types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all training types.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingTypeDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content)
    })
    public ResponseEntity<List<TrainingTypeDTO>> getTrainingTypes() {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Transaction ID: {}, Endpoint: /allTrainingTypes, Request to retrieve all training types", transactionId);

        List<TrainingTypeDTO> trainingTypes = trainingTypeService.getAllTrainingTypes();
        logger.info("Transaction ID: {}, Successfully retrieved {} training types", transactionId, trainingTypes.size());
        return ResponseEntity.ok(trainingTypes);
    }

}
