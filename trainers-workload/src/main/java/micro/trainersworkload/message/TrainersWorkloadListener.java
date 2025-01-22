package micro.trainersworkload.message;

import lombok.RequiredArgsConstructor;
import micro.trainersworkload.dto.ActionEnum;
import micro.trainersworkload.dto.EventDTO;
import micro.trainersworkload.service.TrainersWorkloadService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TrainersWorkloadListener {

      private final TrainersWorkloadService trainersWorkloadService;

      @JmsListener(destination = "training.queue")
      public void receive(
              @Header(name="trainerUsername")String trainerUsername,
              @Header(name="firstName")String firstName,
              @Header(name="lastName")String lastName,
              @Header(name="status")boolean status,
              @Header(name="trainingDate")String date,
              @Header(name="duration")int duration,
              @Header(name="actionType")String actionType
      ){
            LocalDate trainingDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);

            EventDTO event = new EventDTO();
            event.setUsername(trainerUsername);
            event.setFirstName(firstName);
            event.setLastName(lastName);
            event.setStatus(status);
            event.setTrainingDate(trainingDate);
            event.setTrainingDuration(duration);
            event.setAction(ActionEnum.valueOf(actionType));

            trainersWorkloadService.updateTrainerWorkload(event);
      }
}