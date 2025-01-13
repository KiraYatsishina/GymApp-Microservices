package micro.trainersworkload.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import micro.trainersworkload.dto.MonthlySummaryDTO;
import micro.trainersworkload.dto.TrainerWorkloadDTO;
import micro.trainersworkload.service.TrainersWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainer")
public class TrainersWorkloadController {

    private final TrainersWorkloadService trainersWorkloadService;
    private final Logger logger = LoggerFactory.getLogger(TrainersWorkloadController.class);


    @GetMapping("/monthly-workload")
    @CircuitBreaker(name = "trainerWorkload", fallbackMethod = "fallbackTrainerWorkload")
    public ResponseEntity getTrainerWorkload(@RequestBody TrainerWorkloadDTO workloadDTO) {
        String transactionId = UUID.randomUUID().toString();
        String username = workloadDTO.getUserName();

        logger.info("Transaction ID: {}, Endpoint: /trainer/monthly-workload, User: {}, Request: {}",
                transactionId, username, workloadDTO);

        try {
            MonthlySummaryDTO monthlySummaryDTO = trainersWorkloadService.getTrainerWorkload(workloadDTO);

            if (monthlySummaryDTO == null) {
                logger.warn("Transaction ID: {}, User: {}, Response: 404 NOT FOUND", transactionId, username);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            logger.info("Transaction ID: {}, User: {}, Response: 200 OK, Data: {}", transactionId, username, monthlySummaryDTO);
            return new ResponseEntity<>(monthlySummaryDTO, HttpStatus.OK);

        } catch (Exception ex) {
            logger.error("Transaction ID: {}, User: {}, Error: {}", transactionId, username, ex.getMessage(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PutMapping("/update-workload")
//    @CircuitBreaker(name = "updateTrainerWorkload", fallbackMethod = "fallbackUpdateTrainerWorkload")
//    public ResponseEntity updateTrainerWorkload(@RequestBody ActionTrainingDTO actionTrainingDTO) {
//        String transactionId = UUID.randomUUID().toString();
//        String username = actionTrainingDTO.getUserName();
//
//        logger.info("Transaction ID: {}, Endpoint: /trainer/update-workload, User: {}, Request: {}",
//                transactionId, username, actionTrainingDTO);
//
//        try {
//            trainersWorkloadService.updateTrainerWorkload(actionTrainingDTO);
//
//            logger.info("Transaction ID: {}, User: {}, Response: 200 OK", transactionId, username);
//            return new ResponseEntity<>(HttpStatus.OK);
//
//        } catch (Exception ex) {
//            logger.error("Transaction ID: {}, User: {}, Error: {}", transactionId, username, ex.getMessage(), ex);
//            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    public ResponseEntity fallbackTrainerWorkload(Throwable throwable) {
        logger.error("Fallback triggered: TrainerWorkload, Error: {}", throwable.getMessage(), throwable);
        return new ResponseEntity<>("You can not get trainer monthly workload(", HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    public ResponseEntity fallbackUpdateTrainerWorkload(Throwable throwable) {
//        logger.error("Fallback triggered: UpdateTrainerWorkload, Error: {}", throwable.getMessage(), throwable);
//        return new ResponseEntity<>("You can not update trainer monthly workload(", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
