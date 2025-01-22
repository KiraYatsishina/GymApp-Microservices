package micro.trainersworkload.service;

import lombok.RequiredArgsConstructor;
import micro.trainersworkload.dto.EventDTO;
import micro.trainersworkload.dto.TrainerWorkloadRequestDTO;
import micro.trainersworkload.dto.WorkloadResponseDTO;
import micro.trainersworkload.model.Month;
import micro.trainersworkload.model.MonthEnum;
import micro.trainersworkload.model.Workload;
import micro.trainersworkload.model.Year;
import micro.trainersworkload.repository.WorkloadRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainersWorkloadService {

    private final WorkloadRepository workloadRepository;

    public void updateTrainerWorkload(EventDTO event) {

        int month = event.getTrainingDate().getMonth().getValue();
        int year = event.getTrainingDate().getYear();

        Optional<Workload> optionalWorkload = workloadRepository.findByUsername(event.getUsername());
        Workload workload;
        if (optionalWorkload.isEmpty()) {
            workload = new Workload();
            workload.setUsername(event.getUsername());
            workload.setFirstName(event.getFirstName());
            workload.setLastName(event.getLastName());
            workload.setStatus(event.isStatus());
            workload.setYears(new ArrayList<>());

            Year newYear = new Year();
            newYear.setYear(year);
            newYear.setMonths(new ArrayList<>());

            Month newMonth = new Month();
            newMonth.setMonth(MonthEnum.fromNumber(month));
            newMonth.setSummaryDuration(event.getTrainingDuration());

            newYear.getMonths().add(newMonth);
            workload.getYears().add(newYear);
        } else {
            workload = optionalWorkload.get();

            Year yearData = workload.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    Year newYear = new Year();
                    newYear.setYear(year);
                    newYear.setMonths(new ArrayList<>());
                    workload.getYears().add(newYear);
                    return newYear;
                });


            Month monthData = yearData.getMonths().stream()
                .filter(m -> m.getMonth().getNumber() == month)
                .findFirst()
                .orElseGet(() -> {
                    Month newMonth = new Month();
                    newMonth.setMonth(MonthEnum.fromNumber(month));
                    newMonth.setSummaryDuration(0);
                    yearData.getMonths().add(newMonth);
                    return newMonth;
                });

            switch (event.getAction()) {
                case ADD:
                    monthData.setSummaryDuration(
                        monthData.getSummaryDuration() + event.getTrainingDuration()
                    );
                    break;

                case DELETE:
                    int updatedDuration = monthData.getSummaryDuration() - event.getTrainingDuration();
                    monthData.setSummaryDuration(Math.max(updatedDuration, 0));
                    break;

                default:
                    throw new IllegalArgumentException("Invalid action type: " + event.getAction());
            }
        }

        workloadRepository.save(workload);
    }

    public Workload getTrainerWorkload(String username) {
        return workloadRepository.findByUsername(username).get();
    }

    public WorkloadResponseDTO getTrainerWorkloadPerMonth(TrainerWorkloadRequestDTO workloadDTO) {
        Optional<Workload> workload = workloadRepository.findByUsername(workloadDTO.getUserName());

        if (workload.isEmpty()) {
            return WorkloadResponseDTO.builder()
                .userName(workloadDTO.getUserName())
                .year(workloadDTO.getYear())
                .month(workloadDTO.getMonth())
                .workload(0)
                .build();
        }

        Workload trainerWorkload = workload.get();
        List<Year> years = trainerWorkload.getYears();

        Year yearWorkload = years.stream()
            .filter(year -> year.getYear() == workloadDTO.getYear())
            .findFirst()
            .orElse(Year.builder().year(workloadDTO.getYear()).months(new ArrayList<>()).build());

        Month monthWorkload = yearWorkload.getMonths().stream()
            .filter(month -> month.getMonth().getNumber() == workloadDTO.getMonth())
            .findFirst()
            .orElse(Month.builder().month(MonthEnum.fromNumber(workloadDTO.getMonth())).summaryDuration(0).build());

        return WorkloadResponseDTO.builder()
            .userName(workloadDTO.getUserName())
            .year(yearWorkload.getYear())
            .month(monthWorkload.getMonth().getNumber())
            .workload(monthWorkload.getSummaryDuration())
            .build();
    }
}
