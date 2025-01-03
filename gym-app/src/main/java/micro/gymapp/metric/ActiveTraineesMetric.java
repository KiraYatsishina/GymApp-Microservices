package micro.gymapp.metric;

import micro.gymapp.service.TraineeService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ActiveTraineesMetric {

    private final TraineeService traineeService;

    public ActiveTraineesMetric(MeterRegistry meterRegistry, TraineeService traineeService) {
        this.traineeService = traineeService;
        Gauge.builder("trainee.active.count", traineeService, TraineeService::countActiveTrainees)
                .description("Current number of active trainees")
                .register(meterRegistry);
    }
}