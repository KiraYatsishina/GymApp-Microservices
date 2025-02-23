package micro.gymapp.dto.Trainer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupTrainer {
    private String firstName;
    private String lastName;
    private String specialization;
}
