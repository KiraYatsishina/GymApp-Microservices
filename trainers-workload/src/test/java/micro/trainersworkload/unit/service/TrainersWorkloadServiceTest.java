package micro.trainersworkload.unit.service;

import micro.trainersworkload.dto.ActionEnum;
import micro.trainersworkload.dto.EventDTO;
import micro.trainersworkload.dto.TrainerWorkloadRequestDTO;
import micro.trainersworkload.dto.WorkloadResponseDTO;
import micro.trainersworkload.model.Month;
import micro.trainersworkload.model.MonthEnum;
import micro.trainersworkload.model.Workload;
import micro.trainersworkload.model.Year;
import micro.trainersworkload.repository.WorkloadRepository;
import micro.trainersworkload.service.TrainersWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainersWorkloadServiceTest {

    private WorkloadRepository workloadRepository;
    private TrainersWorkloadService service;

    @BeforeEach
    void setUp() {
        workloadRepository = mock(WorkloadRepository.class);
        service = new TrainersWorkloadService(workloadRepository);
    }

    @Test
    void testUpdateTrainerWorkload_AddNewWorkload() {
        EventDTO event = new EventDTO(
            "trainer1",
            "John",
            "Doe",
            true,
            LocalDate.of(2025, 1, 22),
            60,
            ActionEnum.ADD
        );

        when(workloadRepository.findByUsername(event.getUsername())).thenReturn(Optional.empty());

        service.updateTrainerWorkload(event);

        ArgumentCaptor<Workload> captor = ArgumentCaptor.forClass(Workload.class);
        verify(workloadRepository, times(1)).save(captor.capture());

        Workload savedWorkload = captor.getValue();
        assertEquals("trainer1", savedWorkload.getUsername());
        assertEquals("John", savedWorkload.getFirstName());
        assertEquals(60, savedWorkload.getYears().get(0).getMonths().get(0).getSummaryDuration());
    }

    @Test
    void testUpdateTrainerWorkload_UpdateExistingWorkload_Add() {
        EventDTO event = new EventDTO(
            "trainer1",
            "John",
            "Doe",
            true,
            LocalDate.of(2025, 1, 22),
            60,
            ActionEnum.ADD
        );

        Workload existingWorkload = new Workload();
        existingWorkload.setUsername("trainer1");
        Year year = new Year();
        year.setYear(2025);
        Month month = new Month();
        month.setMonth(MonthEnum.JANUARY);
        month.setSummaryDuration(30);
        year.setMonths(List.of(month));
        existingWorkload.setYears(List.of(year));

        when(workloadRepository.findByUsername(event.getUsername())).thenReturn(Optional.of(existingWorkload));

        service.updateTrainerWorkload(event);

        ArgumentCaptor<Workload> captor = ArgumentCaptor.forClass(Workload.class);
        verify(workloadRepository, times(1)).save(captor.capture());

        Workload updatedWorkload = captor.getValue();
        assertEquals(90, updatedWorkload.getYears().get(0).getMonths().get(0).getSummaryDuration());
    }

    @Test
    void testUpdateTrainerWorkload_UpdateExistingWorkload_Delete() {
        EventDTO event = new EventDTO(
            "trainer1",
            "John",
            "Doe",
            true,
            LocalDate.of(2025, 1, 22),
            30,
            ActionEnum.DELETE
        );

        Workload existingWorkload = new Workload();
        existingWorkload.setUsername("trainer1");
        Year year = new Year();
        year.setYear(2025);
        Month month = new Month();
        month.setMonth(MonthEnum.JANUARY);
        month.setSummaryDuration(50);
        year.setMonths(List.of(month));
        existingWorkload.setYears(List.of(year));

        when(workloadRepository.findByUsername(event.getUsername())).thenReturn(Optional.of(existingWorkload));

        service.updateTrainerWorkload(event);

        ArgumentCaptor<Workload> captor = ArgumentCaptor.forClass(Workload.class);
        verify(workloadRepository, times(1)).save(captor.capture());

        Workload updatedWorkload = captor.getValue();
        assertEquals(20, updatedWorkload.getYears().get(0).getMonths().get(0).getSummaryDuration());
    }

    @Test
    void testGetTrainerWorkload() {
        Workload workload = new Workload();
        workload.setUsername("trainer1");

        when(workloadRepository.findByUsername("trainer1")).thenReturn(Optional.of(workload));

        Workload result = service.getTrainerWorkload("trainer1");

        assertEquals("trainer1", result.getUsername());
    }

    @Test
    void testGetTrainerWorkloadPerMonth_NoWorkload() {
        TrainerWorkloadRequestDTO requestDTO = new TrainerWorkloadRequestDTO("trainer1", 2025, 1);

        when(workloadRepository.findByUsername("trainer1")).thenReturn(Optional.empty());

        WorkloadResponseDTO response = service.getTrainerWorkloadPerMonth(requestDTO);

        assertEquals("trainer1", response.getUserName());
        assertEquals(2025, response.getYear());
        assertEquals(1, response.getMonth());
        assertEquals(0, response.getWorkload());
    }

    @Test
    void testGetTrainerWorkloadPerMonth_ExistingWorkload() {
        TrainerWorkloadRequestDTO requestDTO = new TrainerWorkloadRequestDTO("trainer1", 2025, 1);

        Workload workload = new Workload();
        workload.setUsername("trainer1");
        Year year = new Year();
        year.setYear(2025);
        Month month = new Month();
        month.setMonth(MonthEnum.JANUARY);
        month.setSummaryDuration(100);
        year.setMonths(List.of(month));
        workload.setYears(List.of(year));

        when(workloadRepository.findByUsername("trainer1")).thenReturn(Optional.of(workload));

        WorkloadResponseDTO response = service.getTrainerWorkloadPerMonth(requestDTO);

        assertEquals("trainer1", response.getUserName());
        assertEquals(2025, response.getYear());
        assertEquals(1, response.getMonth());
        assertEquals(100, response.getWorkload());
    }
}

