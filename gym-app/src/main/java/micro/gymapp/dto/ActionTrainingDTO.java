package micro.gymapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionTrainingDTO {
    private String userName;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String trainingDate;
    private int duration;
    private String actionType;
}
