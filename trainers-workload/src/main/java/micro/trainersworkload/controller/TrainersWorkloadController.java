package micro.trainersworkload.controller;

import lombok.RequiredArgsConstructor;
import micro.trainersworkload.dto.ActionTrainingDTO;
import micro.trainersworkload.dto.MonthlySummaryDTO;
import micro.trainersworkload.dto.TrainerWorkloadDTO;
import micro.trainersworkload.service.TrainersWorkloadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainer")
public class TrainersWorkloadController {

    private final TrainersWorkloadService trainersWorkloadService;

    @GetMapping()
    public ResponseEntity<MonthlySummaryDTO> getTrainerWorkload(TrainerWorkloadDTO workloadDTO) {
        return new ResponseEntity<>(new MonthlySummaryDTO(), HttpStatus.OK);
    }

    @PostMapping("action-training-duration")
    public ResponseEntity actionTrainingDuration(ActionTrainingDTO trainingDTO){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/check")
    //@PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ResponseEntity<String> checkTraining(){
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
