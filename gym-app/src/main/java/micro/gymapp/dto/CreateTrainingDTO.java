package micro.gymapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrainingDTO {
    private String traineeUsername;
    private String trainerUsername;
    private String name;
    private LocalDate date;
    private String type;
    private int duration;
}
