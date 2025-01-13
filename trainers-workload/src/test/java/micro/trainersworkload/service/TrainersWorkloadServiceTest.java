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
    private WorkloadRepository workloadRepository;

    @Mock
    private TrainersWorkloadController trainersWorkloadController;

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
    void testUpdateTrainerWorkload_NewTrainerAndWorkload() {

    }

    @Test
    void testUpdateTrainerWorkload_ExistingTrainerAndWorkload_AddAction() {

    }

    @Test
    void testUpdateTrainerWorkload_ExistingTrainerAndWorkload_DeleteAction() {

    }
    
    
    @Test
    void testGetTrainerWorkload_TrainerNotFound() {

    }

    @Test
    void testGetTrainerWorkload_WorkloadNotFound() {

    }

    @Test
    void testGetTrainerWorkload_Success() {

    }
}