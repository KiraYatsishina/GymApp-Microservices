package micro.gymapp.metric;

import io.micrometer.core.instrument.MeterRegistry;
import micro.gymapp.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ActiveTraineesMetricTest {

      @Mock
      private MeterRegistry meterRegistry;

      @Mock
      private TraineeService traineeService;

      @BeforeEach
      void setUp() {
            MockitoAnnotations.openMocks(this);
      }

      @Test
      void testTraineeServiceIntegration() {
            when(traineeService.countActiveTrainees()).thenReturn(20l);

            new ActiveTraineesMetric(meterRegistry, traineeService);

            assertEquals(20, traineeService.countActiveTrainees());
      }
}
