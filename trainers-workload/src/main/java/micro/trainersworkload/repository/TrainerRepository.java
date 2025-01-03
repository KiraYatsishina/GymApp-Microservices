package micro.trainersworkload.repository;

import micro.trainersworkload.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer,Long> {

    Trainer findByUsername(String username);
}
