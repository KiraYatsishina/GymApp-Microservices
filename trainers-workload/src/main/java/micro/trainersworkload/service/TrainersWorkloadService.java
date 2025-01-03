package micro.trainersworkload.service;

import lombok.RequiredArgsConstructor;
import micro.trainersworkload.dto.ActionTrainingDTO;
import micro.trainersworkload.dto.MonthlySummaryDTO;
import micro.trainersworkload.dto.TrainerWorkloadDTO;
import micro.trainersworkload.model.Trainer;
import micro.trainersworkload.model.Workload;
import micro.trainersworkload.repository.TrainerRepository;
import micro.trainersworkload.repository.WorkloadRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainersWorkloadService {

    private final TrainerRepository trainerRepository;
    private final WorkloadRepository workloadRepository;

    public void updateTrainerWorkload(ActionTrainingDTO actionTrainingDTO) {
        Trainer trainer = trainerRepository.findByUsername(actionTrainingDTO.getUserName());
        if (trainer == null) {
            trainer = new Trainer();
            trainer.setUsername(actionTrainingDTO.getUserName());
            trainer.setFirstName(actionTrainingDTO.getFirstName());
            trainer.setLastName(actionTrainingDTO.getLastName());
            trainer.setStatus(actionTrainingDTO.isActive());
            trainer = trainerRepository.save(trainer);
        }

        // Определить месяц и год тренировки
        LocalDate trainingDate = LocalDate.parse(actionTrainingDTO.getTrainingDate());
        int month = trainingDate.getMonth().getValue();
        int year = trainingDate.getYear();

        // Найти или создать запись о нагрузке
        Workload workload = workloadRepository.findByTrainerAndWorkloadYearAndWorkloadMonth(trainer, year, month);
        if (workload == null) {
            workload = new Workload();
            workload.setTrainer(trainer);
            workload.setWorkloadYear(year);
            workload.setWorkloadMonth(month);
            workload.setTotalDuration(0);
            workload = workloadRepository.save(workload);
        }

        // Обновить нагрузку в зависимости от типа действия
        if ("ADD".equalsIgnoreCase(actionTrainingDTO.getActionType())) {
            workload.setTotalDuration(workload.getTotalDuration() + actionTrainingDTO.getDuration());
        } else if ("DELETE".equalsIgnoreCase(actionTrainingDTO.getActionType())) {
            workload.setTotalDuration(Math.max(0, workload.getTotalDuration() - actionTrainingDTO.getDuration()));
        }

        workloadRepository.save(workload);
    }

    public MonthlySummaryDTO getTrainerWorkload(TrainerWorkloadDTO workloadDTO) {

        Trainer trainer = trainerRepository.findByUsername(workloadDTO.getUserName());
        if (trainer == null) {
            return null;
        }

        // Получить нагрузку за указанный год и месяц
        Workload workload = workloadRepository.findByTrainerAndWorkloadYearAndWorkloadMonth(
                trainer, workloadDTO.getYear(), workloadDTO.getMonth()
        );

        // Если данных о нагрузке нет, вернуть пустую информацию
        int totalDuration = (workload != null) ? workload.getTotalDuration() : 0;

        // Сформировать ответ
        MonthlySummaryDTO summaryDTO = new MonthlySummaryDTO();
        summaryDTO.setYear(workloadDTO.getYear());
        summaryDTO.setMonth(workloadDTO.getMonth());
        summaryDTO.setTotalDuration(totalDuration);

        return summaryDTO;
    }
}
