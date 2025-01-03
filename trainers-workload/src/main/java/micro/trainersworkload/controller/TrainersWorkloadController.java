package micro.trainersworkload.controller;

import lombok.RequiredArgsConstructor;
import micro.trainersworkload.dto.ActionTrainingDTO;
import micro.trainersworkload.dto.MonthlySummaryDTO;
import micro.trainersworkload.dto.TrainerWorkloadDTO;
import micro.trainersworkload.service.TrainersWorkloadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainer")
public class TrainersWorkloadController {

    private final TrainersWorkloadService trainersWorkloadService;

    @GetMapping("/monthly-workload")
    public ResponseEntity<MonthlySummaryDTO> getTrainerWorkload(@RequestBody TrainerWorkloadDTO workloadDTO) {
        MonthlySummaryDTO monthlySummaryDTO = trainersWorkloadService.getTrainerWorkload(workloadDTO);
        if(monthlySummaryDTO == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(monthlySummaryDTO, HttpStatus.OK);
    }

    @PutMapping("/update-workload")
    public ResponseEntity<Void> updateTrainerWorkload(@RequestBody ActionTrainingDTO actionTrainingDTO) {
        trainersWorkloadService.updateTrainerWorkload(actionTrainingDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
