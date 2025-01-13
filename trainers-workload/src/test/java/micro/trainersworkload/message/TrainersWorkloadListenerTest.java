package micro.trainersworkload.message;

import micro.trainersworkload.service.TrainersWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

class TrainersWorkloadListenerTest {

      @Mock
      private TrainersWorkloadService trainersWorkloadService;

      @Mock
      private Logger logger;

      @InjectMocks
      private TrainersWorkloadListener trainersWorkloadListener;

      @BeforeEach
      void setUp() {
            MockitoAnnotations.openMocks(this);
      }

      @Test
      void receive_ShouldCallServiceWithCorrectParameters() {
            String trainerUsername = "testTrainer";
            String date = "2025-01-15";
            int duration = 60;
            String actionType = "ADD";

            trainersWorkloadListener.receive(trainerUsername, date, duration, actionType);

            verify(trainersWorkloadService, times(1))
                    .updateTrainerWorkload(trainerUsername, date, duration, actionType);
      }

}
