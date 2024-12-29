package micro.trainersworkload.repository;

import micro.trainersworkload.model.Trainer;
import micro.trainersworkload.model.Workload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkloadRepository extends JpaRepository<Workload,Long> {
}
