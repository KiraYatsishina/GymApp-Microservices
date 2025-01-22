package micro.trainersworkload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkloadResponseDTO {

  private String userName;
  private int year;
  private int month;
  private int workload;
}
