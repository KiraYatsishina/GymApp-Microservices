package micro.trainersworkload.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name="trainers")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="trainer_id")
    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private boolean status;

    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY)
    private Set<Workload> workloads;

    private int totalDuration;
}
