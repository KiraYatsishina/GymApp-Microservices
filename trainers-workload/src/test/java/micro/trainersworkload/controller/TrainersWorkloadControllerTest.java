package micro.trainersworkload.controller;

import micro.trainersworkload.dto.MonthlySummaryDTO;
import micro.trainersworkload.dto.TrainerWorkloadDTO;
import micro.trainersworkload.service.TrainersWorkloadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainersWorkloadControllerTest {
    @Mock
    private TrainersWorkloadService trainersWorkloadService;

    @InjectMocks
    private TrainersWorkloadController trainersWorkloadController;

    @Test
    void testGetTrainerWorkload_Success() {
        TrainerWorkloadDTO workloadDTO = new TrainerWorkloadDTO();
        workloadDTO.setUserName("trainer1");

        MonthlySummaryDTO monthlySummaryDTO = new MonthlySummaryDTO();
        when(trainersWorkloadService.getTrainerWorkload(any())).thenReturn(monthlySummaryDTO);

        ResponseEntity<?> response = trainersWorkloadController.getTrainerWorkload(workloadDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(monthlySummaryDTO, response.getBody());
    }

    @Test
    void testGetTrainerWorkload_NotFound() {
        TrainerWorkloadDTO workloadDTO = new TrainerWorkloadDTO();
        workloadDTO.setUserName("trainer1");

        when(trainersWorkloadService.getTrainerWorkload(any())).thenReturn(null);

        ResponseEntity<?> response = trainersWorkloadController.getTrainerWorkload(workloadDTO);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetTrainerWorkload_InternalServerError() {
        TrainerWorkloadDTO workloadDTO = new TrainerWorkloadDTO();
        workloadDTO.setUserName("trainer1");

        when(trainersWorkloadService.getTrainerWorkload(any())).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<?> response = trainersWorkloadController.getTrainerWorkload(workloadDTO);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Service error", response.getBody());
    }

    @Test
    void testFallbackTrainerWorkload() {
        Throwable throwable = new RuntimeException("Circuit breaker triggered");

        ResponseEntity<?> response = trainersWorkloadController.fallbackTrainerWorkload(throwable);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("You can not get trainer monthly workload(", response.getBody());
    }
}
