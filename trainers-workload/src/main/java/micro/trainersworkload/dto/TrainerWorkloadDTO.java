package micro.trainersworkload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadDTO {
    private String userName;
    private int year;
    private int month;
}
