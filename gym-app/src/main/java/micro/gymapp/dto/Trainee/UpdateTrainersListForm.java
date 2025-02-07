package micro.gymapp.dto.Trainee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTrainersListForm {

  private String username;
  private List<String> trainerUsernames;
}
