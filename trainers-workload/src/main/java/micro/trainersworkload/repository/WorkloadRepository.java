package micro.trainersworkload.repository;

import micro.trainersworkload.model.Workload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkloadRepository extends JpaRepository<Workload,Long> {

    Workload findByTrainersUsernameAndWorkloadYearAndWorkloadMonth(String trainerUsername, int year, int month);
}
