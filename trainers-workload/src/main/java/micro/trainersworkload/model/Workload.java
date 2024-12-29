package micro.trainersworkload.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="workloads")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Workload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="workload_id")

    private Long id;

    @Column(name = "workload_year", nullable = false)
    private int workloadYear;

    @Column(name = "workload_month", nullable = false)
    private int workloadMonth;

    private int totalDuration;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;
}
