package micro.gymapp.dto.Trainee;


import micro.gymapp.dto.Trainer.ShortTrainerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeDTO {
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
    private List<ShortTrainerDTO> trainers;
}