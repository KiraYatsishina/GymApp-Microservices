package micro.trainersworkload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummaryDTO {
    private int year;
    private int month;
    private int totalDuration;
}
