package micro.trainersworkload.service;

import micro.trainersworkload.controller.TrainersWorkloadController;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainersWorkloadServiceTest {
    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private WorkloadRepository workloadRepository;

    @Mock
    private TrainersWorkloadController trainersWorkloadController;

    @InjectMocks
    private TrainersWorkloadService trainersWorkloadService;

    private ActionTrainingDTO actionTrainingDTO;
    private TrainerWorkloadDTO trainerWorkloadDTO;

    @BeforeEach
    void setUp() {
        actionTrainingDTO = new ActionTrainingDTO();
        actionTrainingDTO.setUserName("John.Doe");
        actionTrainingDTO.setFirstName("John");
        actionTrainingDTO.setLastName("Doe");
        actionTrainingDTO.setActive(true);
        actionTrainingDTO.setTrainingDate("2025-01-01");
        actionTrainingDTO.setActionType("ADD");
        actionTrainingDTO.setDuration(60);

        trainerWorkloadDTO = new TrainerWorkloadDTO();
        trainerWorkloadDTO.setUserName("John.Doe");
        trainerWorkloadDTO.setYear(2025);
        trainerWorkloadDTO.setMonth(1);
    }

    @Test
    void testUpdateTrainerWorkload_NewTrainerAndWorkload() {
        when(trainerRepository.findByUsername(any())).thenReturn(null);
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(workloadRepository.findByTrainerAndWorkloadYearAndWorkloadMonth(any(), anyInt(), anyInt())).thenReturn(null);
        when(workloadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        trainersWorkloadService.updateTrainerWorkload(actionTrainingDTO);

        verify(trainerRepository).save(any(Trainer.class));
        verify(workloadRepository, times(2)).save(any(Workload.class));
    }

    @Test
    void testUpdateTrainerWorkload_ExistingTrainerAndWorkload_AddAction() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Doe");

        Workload workload = new Workload();
        workload.setTrainer(trainer);
        workload.setWorkloadYear(2025);
        workload.setWorkloadMonth(1);
        workload.setTotalDuration(100);

        when(trainerRepository.findByUsername(any())).thenReturn(trainer);
        when(workloadRepository.findByTrainerAndWorkloadYearAndWorkloadMonth(any(), anyInt(), anyInt())).thenReturn(workload);

        trainersWorkloadService.updateTrainerWorkload(actionTrainingDTO);

        assertEquals(160, workload.getTotalDuration());
        verify(workloadRepository).save(workload);
    }

    @Test
    void testUpdateTrainerWorkload_ExistingTrainerAndWorkload_DeleteAction() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Doe");

        Workload workload = new Workload();
        workload.setTrainer(trainer);
        workload.setWorkloadYear(2025);
        workload.setWorkloadMonth(1);
        workload.setTotalDuration(100);

        actionTrainingDTO.setActionType("DELETE");
        actionTrainingDTO.setDuration(50);

        when(trainerRepository.findByUsername(any())).thenReturn(trainer);
        when(workloadRepository.findByTrainerAndWorkloadYearAndWorkloadMonth(any(), anyInt(), anyInt())).thenReturn(workload);

        trainersWorkloadService.updateTrainerWorkload(actionTrainingDTO);

        assertEquals(50, workload.getTotalDuration());
        verify(workloadRepository).save(workload);
    }
    
    
    @Test
    void testGetTrainerWorkload_TrainerNotFound() {
        when(trainerRepository.findByUsername(any())).thenReturn(null);

        MonthlySummaryDTO result = trainersWorkloadService.getTrainerWorkload(trainerWorkloadDTO);

        assertNull(result);
    }

    @Test
    void testGetTrainerWorkload_WorkloadNotFound() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Doe");

        when(trainerRepository.findByUsername(any())).thenReturn(trainer);
        when(workloadRepository.findByTrainerAndWorkloadYearAndWorkloadMonth(any(), anyInt(), anyInt())).thenReturn(null);

        MonthlySummaryDTO result = trainersWorkloadService.getTrainerWorkload(trainerWorkloadDTO);

        assertNotNull(result);
        assertEquals(0, result.getTotalDuration());
    }

    @Test
    void testGetTrainerWorkload_Success() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Doe");

        Workload workload = new Workload();
        workload.setTrainer(trainer);
        workload.setWorkloadYear(2025);
        workload.setWorkloadMonth(1);
        workload.setTotalDuration(100);

        when(trainerRepository.findByUsername(any())).thenReturn(trainer);
        when(workloadRepository.findByTrainerAndWorkloadYearAndWorkloadMonth(any(), anyInt(), anyInt())).thenReturn(workload);

        MonthlySummaryDTO result = trainersWorkloadService.getTrainerWorkload(trainerWorkloadDTO);

        assertNotNull(result);
        assertEquals(100, result.getTotalDuration());
        assertEquals(2025, result.getYear());
        assertEquals(1, result.getMonth());
    }
}