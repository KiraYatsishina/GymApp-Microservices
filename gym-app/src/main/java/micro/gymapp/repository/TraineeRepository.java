package micro.gymapp.repository;

import micro.gymapp.model.Trainee;
import micro.gymapp.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long>{
    Optional<Trainee> findByUsername(String username);

    Trainee save(Trainee trainee);

    @Query("SELECT t FROM Trainer t WHERE t NOT IN (SELECT tr FROM Trainee tra JOIN tra.trainers tr WHERE tra.userId = :userId)")
    List<Trainer> findNotAssignedTrainers(@Param("userId") Long userId);

    @Query("SELECT t FROM Trainer t WHERE t.username IN :usernames")
    List<Trainer> findByUsernameIn(List<String> usernames);

    long countByIsActive(boolean isActive);
}
