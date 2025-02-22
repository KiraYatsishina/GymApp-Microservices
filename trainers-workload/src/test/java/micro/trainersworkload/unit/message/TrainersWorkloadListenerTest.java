package micro.trainersworkload.unit.message;

import micro.trainersworkload.dto.ActionEnum;
import micro.trainersworkload.dto.EventDTO;
import micro.trainersworkload.message.TrainersWorkloadListener;
import micro.trainersworkload.service.TrainersWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TrainersWorkloadListenerTest {

      private TrainersWorkloadService trainersWorkloadService;
      private TrainersWorkloadListener listener;

      @BeforeEach
      void setUp() {
            trainersWorkloadService = mock(TrainersWorkloadService.class);
            listener = new TrainersWorkloadListener(trainersWorkloadService);
      }

      @Test
      void testReceive() {

            String trainerUsername = "trainer1";
            String firstName = "John";
            String lastName = "Doe";
            boolean status = true;
            String trainingDateStr = "2025-01-22";
            int duration = 60;
            String actionType = "ADD";

            listener.receive(trainerUsername, firstName, lastName, status, trainingDateStr, duration, actionType);

            ArgumentCaptor<EventDTO> captor = ArgumentCaptor.forClass(EventDTO.class);
            verify(trainersWorkloadService, times(1)).updateTrainerWorkload(captor.capture());

            EventDTO capturedEvent = captor.getValue();
            assertEquals(trainerUsername, capturedEvent.getUsername());
            assertEquals(firstName, capturedEvent.getFirstName());
            assertEquals(lastName, capturedEvent.getLastName());
            assertEquals(status, capturedEvent.isStatus());
            assertEquals(LocalDate.parse(trainingDateStr), capturedEvent.getTrainingDate());
            assertEquals(duration, capturedEvent.getTrainingDuration());
            assertEquals(ActionEnum.ADD, capturedEvent.getAction());
      }

      @Test
      void testReceiveWithInvalidActionType() {
            String trainerUsername = "trainer1";
            String firstName = "John";
            String lastName = "Doe";
            boolean status = true;
            String trainingDateStr = "2025-01-22";
            int duration = 60;
            String invalidActionType = "INVALID_ACTION";

            assertThrows(IllegalArgumentException.class, () ->
                listener.receive(trainerUsername, firstName, lastName, status, trainingDateStr, duration, invalidActionType)
            );

            verify(trainersWorkloadService, never()).updateTrainerWorkload(any(EventDTO.class));
      }
}
