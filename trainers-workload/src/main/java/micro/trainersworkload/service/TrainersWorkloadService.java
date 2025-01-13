package micro.trainersworkload.service;

import lombok.RequiredArgsConstructor;
import micro.trainersworkload.dto.MonthlySummaryDTO;
import micro.trainersworkload.dto.TrainerWorkloadDTO;
import micro.trainersworkload.model.Workload;
import micro.trainersworkload.repository.WorkloadRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TrainersWorkloadService {

    private final WorkloadRepository workloadRepository;

    public void updateTrainerWorkload(String trainerUsername, String date, int duration, String actionType) {

        LocalDate trainingDate = LocalDate.parse(date);
        int month = trainingDate.getMonth().getValue();
        int year = trainingDate.getYear();

        Workload workload = workloadRepository.findByTrainersUsernameAndWorkloadYearAndWorkloadMonth(trainerUsername, year, month);
        if (workload == null) {
            workload = new Workload();
            workload.setTrainersUsername(trainerUsername);
            workload.setWorkloadYear(year);
            workload.setWorkloadMonth(month);
            workload.setTotalDuration(0);
            try {
                workload = workloadRepository.save(workload);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        switch (actionType.toUpperCase()) {
            case "ADD":
                workload.setTotalDuration(workload.getTotalDuration() + duration);
                break;
            case "DELETE":
                workload.setTotalDuration(Math.max(0, workload.getTotalDuration() - duration));
                break;
            default:
                throw new IllegalArgumentException("Invalid action type: " + duration);
        }
        workloadRepository.save(workload);
    }

    public MonthlySummaryDTO getTrainerWorkload(TrainerWorkloadDTO workloadDTO) {

        Workload workload = workloadRepository.findByTrainersUsernameAndWorkloadYearAndWorkloadMonth(
                workloadDTO.getUserName(), workloadDTO.getYear(), workloadDTO.getMonth()
        );

        int totalDuration = (workload != null) ? workload.getTotalDuration() : 0;

        MonthlySummaryDTO summaryDTO = new MonthlySummaryDTO();
        summaryDTO.setYear(workloadDTO.getYear());
        summaryDTO.setMonth(workloadDTO.getMonth());
        summaryDTO.setTotalDuration(totalDuration);

        return summaryDTO;
    }
}
