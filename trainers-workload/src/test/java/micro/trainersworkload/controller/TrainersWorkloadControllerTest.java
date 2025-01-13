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
    void testUpdateTrainerWorkload_Success() {
        ActionTrainingDTO actionTrainingDTO = new ActionTrainingDTO();
        actionTrainingDTO.setUserName("trainer1");
        actionTrainingDTO.setFirstName("John");
        actionTrainingDTO.setLastName("Doe");
        actionTrainingDTO.setTrainingDate("2025-01-01");
        actionTrainingDTO.setDuration(60);
        actionTrainingDTO.setActionType("ADD");
        actionTrainingDTO.setActive(true);

        ResponseEntity<?> response = trainersWorkloadController.updateTrainerWorkload(actionTrainingDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trainersWorkloadService, times(1)).updateTrainerWorkload(actionTrainingDTO);
    }

    @Test
    void testUpdateTrainerWorkload_InternalServerError() {
        ActionTrainingDTO actionTrainingDTO = new ActionTrainingDTO();
        actionTrainingDTO.setUserName("trainer1");
        actionTrainingDTO.setFirstName("John");
        actionTrainingDTO.setLastName("Doe");
        actionTrainingDTO.setTrainingDate("2025-01-01");
        actionTrainingDTO.setDuration(60);
        actionTrainingDTO.setActionType("ADD");
        actionTrainingDTO.setActive(true);

        doThrow(new RuntimeException("Service error")).when(trainersWorkloadService).updateTrainerWorkload(any());

        ResponseEntity<?> response = trainersWorkloadController.updateTrainerWorkload(actionTrainingDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Service error", response.getBody());
        verify(trainersWorkloadService, times(1)).updateTrainerWorkload(actionTrainingDTO);
    }

    @Test
    void testFallbackUpdateTrainerWorkload() {
        Throwable throwable = new RuntimeException("Circuit breaker triggered");

        ResponseEntity<?> response = trainersWorkloadController.fallbackUpdateTrainerWorkload(throwable);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("You can not update trainer monthly workload(", response.getBody());
    }

    @Test
    void testFallbackTrainerWorkload() {
        Throwable throwable = new RuntimeException("Circuit breaker triggered");

        ResponseEntity<?> response = trainersWorkloadController.fallbackTrainerWorkload(throwable);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("You can not get trainer monthly workload(", response.getBody());
    }
}
