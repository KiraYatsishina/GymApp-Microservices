package micro.trainersworkload.controller;

import micro.trainersworkload.dto.TrainerWorkloadRequestDTO;
import micro.trainersworkload.dto.WorkloadResponseDTO;
import micro.trainersworkload.model.Workload;
import micro.trainersworkload.service.TrainersWorkloadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainersWorkloadControllerTest {
    @Mock
    private TrainersWorkloadService trainersWorkloadService;

    @InjectMocks
    private TrainersWorkloadController trainersWorkloadController;

    @Test
    void testGetTrainerWorkloadPerMonth_Success() {
        TrainerWorkloadRequestDTO requestDTO = new TrainerWorkloadRequestDTO("JohnDoe", 2025, 3);
        WorkloadResponseDTO responseDTO = new WorkloadResponseDTO("JohnDoe", 2025, 3, 180);

        when(trainersWorkloadService.getTrainerWorkloadPerMonth(requestDTO)).thenReturn(responseDTO);

        ResponseEntity result = trainersWorkloadController.getTrainerWorkloadPerMonth(requestDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(responseDTO, result.getBody());
        verify(trainersWorkloadService, times(1)).getTrainerWorkloadPerMonth(requestDTO);
    }

    @Test
    void testGetTrainerWorkloadPerMonth_NotFound() {
        TrainerWorkloadRequestDTO requestDTO = new TrainerWorkloadRequestDTO("JohnDoe", 2025, 3);

        when(trainersWorkloadService.getTrainerWorkloadPerMonth(requestDTO)).thenReturn(null);

        ResponseEntity result = trainersWorkloadController.getTrainerWorkloadPerMonth(requestDTO);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
        verify(trainersWorkloadService, times(1)).getTrainerWorkloadPerMonth(requestDTO);
    }

    @Test
    void testGetTrainerWorkloadPerMonth_InternalServerError() {
        TrainerWorkloadRequestDTO requestDTO = new TrainerWorkloadRequestDTO("JohnDoe", 2025, 3);

        when(trainersWorkloadService.getTrainerWorkloadPerMonth(requestDTO)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity result = trainersWorkloadController.getTrainerWorkloadPerMonth(requestDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Unexpected error", result.getBody());
        verify(trainersWorkloadService, times(1)).getTrainerWorkloadPerMonth(requestDTO);
    }

    @Test
    void testGetTrainerWorkload_Success() {
        String username = "JohnDoe";
        Workload workload = new Workload();

        when(trainersWorkloadService.getTrainerWorkload(username)).thenReturn(workload);

        ResponseEntity<Workload> result = trainersWorkloadController.getTrainerWorkload(username);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(workload, result.getBody());
        verify(trainersWorkloadService, times(1)).getTrainerWorkload(username);
    }

    @Test
    void testFallbackTrainerWorkload() {
        Throwable throwable = new RuntimeException("Service unavailable");

        ResponseEntity result = trainersWorkloadController.fallbackTrainerWorkload(throwable);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("You can not get trainer monthly workload(", result.getBody());
    }
}
