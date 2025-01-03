package micro.gymapp.mapper;

import micro.gymapp.dto.Trainer.ShortTrainerDTO;
import micro.gymapp.dto.Trainer.TrainerDTO;
import micro.gymapp.model.Trainer;

import java.util.stream.Collectors;

public class TrainerMapper {
    public static TrainerDTO toDTO(Trainer trainer, boolean hasTraineeList) {
        return new TrainerDTO(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                trainer.getSpecialization().getTrainingTypeName().name(),
                hasTraineeList ? trainer.getTrainees().stream()
                        .map(TraineeMapper::toShortDTO)
                        .collect(Collectors.toList()) : null
        );
    }

    public static ShortTrainerDTO toShortDTO(Trainer trainer) {
        return new ShortTrainerDTO(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getSpecialization().getTrainingTypeName().name()
        );
    }
}
