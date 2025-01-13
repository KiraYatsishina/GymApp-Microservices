package micro.trainersworkload.service;

import micro.trainersworkload.dto.MonthlySummaryDTO;
import micro.trainersworkload.dto.TrainerWorkloadDTO;
import micro.trainersworkload.model.Workload;
import micro.trainersworkload.repository.WorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainersWorkloadServiceTest {

    @Mock
    private WorkloadRepository workloadRepository;

    @InjectMocks
    private TrainersWorkloadService trainersWorkloadService;

    private TrainerWorkloadDTO trainerWorkloadDTO;

    @BeforeEach
    void setUp() {
        trainerWorkloadDTO = new TrainerWorkloadDTO();
        trainerWorkloadDTO.setUserName("John.Doe");
        trainerWorkloadDTO.setYear(2025);
        trainerWorkloadDTO.setMonth(1);
    }

    @Test
    void testUpdateTrainerWorkload_ExistingTrainerAndWorkload_AddAction() {
        String trainerUsername = "John.Doe";
        String date = "2025-01-15";
        int duration = 60;
        String actionType = "ADD";
        Workload existingWorkload = new Workload(1L, 2025, 1, 120, trainerUsername);

        when(workloadRepository.findByTrainersUsernameAndWorkloadYearAndWorkloadMonth(trainerUsername, 2025, 1))
                .thenReturn(existingWorkload);

        trainersWorkloadService.updateTrainerWorkload(trainerUsername, date, duration, actionType);

        assertEquals(180, existingWorkload.getTotalDuration());
        verify(workloadRepository, times(1)).save(existingWorkload);
    }

    @Test
    void testUpdateTrainerWorkload_ExistingTrainerAndWorkload_DeleteAction() {
        String trainerUsername = "John.Doe";
        String date = "2025-01-15";
        int duration = 60;
        String actionType = "DELETE";
        Workload existingWorkload = new Workload(1L, 2025, 1, 120, trainerUsername);

        when(workloadRepository.findByTrainersUsernameAndWorkloadYearAndWorkloadMonth(trainerUsername, 2025, 1))
                .thenReturn(existingWorkload);

        trainersWorkloadService.updateTrainerWorkload(trainerUsername, date, duration, actionType);

        assertEquals(60, existingWorkload.getTotalDuration());
        verify(workloadRepository, times(1)).save(existingWorkload);
    }

    @Test
    void testGetTrainerWorkload_TrainerNotFound() {
        trainerWorkloadDTO.setUserName("UnknownTrainer");

        when(workloadRepository.findByTrainersUsernameAndWorkloadYearAndWorkloadMonth(
                trainerWorkloadDTO.getUserName(), trainerWorkloadDTO.getYear(), trainerWorkloadDTO.getMonth()))
                .thenReturn(null);

        MonthlySummaryDTO summary = trainersWorkloadService.getTrainerWorkload(trainerWorkloadDTO);

        assertNotNull(summary);
        assertEquals(2025, summary.getYear());
        assertEquals(1, summary.getMonth());
        assertEquals(0, summary.getTotalDuration());
    }

    @Test
    void testGetTrainerWorkload_WorkloadNotFound() {
        when(workloadRepository.findByTrainersUsernameAndWorkloadYearAndWorkloadMonth(
                trainerWorkloadDTO.getUserName(), trainerWorkloadDTO.getYear(), trainerWorkloadDTO.getMonth()))
                .thenReturn(null);

        MonthlySummaryDTO summary = trainersWorkloadService.getTrainerWorkload(trainerWorkloadDTO);

        assertNotNull(summary);
        assertEquals(0, summary.getTotalDuration());
    }

    @Test
    void testGetTrainerWorkload_Success() {
        Workload workload = new Workload(1L, 2025, 1, 120, "John.Doe");
        when(workloadRepository.findByTrainersUsernameAndWorkloadYearAndWorkloadMonth(
                trainerWorkloadDTO.getUserName(), trainerWorkloadDTO.getYear(), trainerWorkloadDTO.getMonth()))
                .thenReturn(workload);

        MonthlySummaryDTO summary = trainersWorkloadService.getTrainerWorkload(trainerWorkloadDTO);

        assertNotNull(summary);
        assertEquals(120, summary.getTotalDuration());
        assertEquals(2025, summary.getYear());
        assertEquals(1, summary.getMonth());
    }
}
