package micro.trainersworkload.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Document(collection = "workloads")
public class Workload {

    @Id
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private List<Year> years;
}
