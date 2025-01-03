package micro.gymapp.client;

import micro.gymapp.dto.ActionTrainingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("trainers-workload")
public interface TrainersWorkloadClient {

    @PutMapping("/workload/trainer/update-workload")
    ResponseEntity<Void> updateTrainerWorkload(@RequestBody ActionTrainingDTO actionTrainingDTO);

}
