package micro.trainersworkload.message;

import lombok.RequiredArgsConstructor;
import micro.trainersworkload.service.TrainersWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainersWorkloadListener {

      private static final Logger logger = LoggerFactory.getLogger(TrainersWorkloadListener.class);
      private final TrainersWorkloadService trainersWorkloadService;

      @JmsListener(destination = "training.queue")
      public void receive(
              @Header(name="trainerUsername")String trainerUsername,
              @Header(name="trainingDate")String date,
              @Header(name="duration")int duration,
              @Header(name="actionType")String actionType
      ){
           trainersWorkloadService.updateTrainerWorkload(trainerUsername, date, duration, actionType);
      }
}