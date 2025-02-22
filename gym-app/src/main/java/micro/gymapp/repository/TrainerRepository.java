package micro.gymapp.repository;

import micro.gymapp.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUsername(String username);
    Trainer save(Trainer trainer);

    @Query("SELECT t FROM Trainer t WHERE t.username IN :usernames")
    List<Trainer> findByUsernameIn(List<String> usernames);

}
