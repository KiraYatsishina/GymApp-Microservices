package micro.gymapp.repository;

import micro.gymapp.model.TrainingType;
import micro.gymapp.model.TrainingTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
    Optional<TrainingType> findByTrainingTypeName(TrainingTypeEnum trainingTypeEnum);
    List<TrainingType> findAll();
}
