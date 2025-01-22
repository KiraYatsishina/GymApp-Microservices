package micro.trainersworkload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private LocalDate trainingDate;
    private int trainingDuration;
    private ActionEnum action;
}
