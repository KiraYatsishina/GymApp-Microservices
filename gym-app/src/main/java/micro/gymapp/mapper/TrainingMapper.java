package micro.gymapp.mapper;

import micro.gymapp.dto.TrainingDTO;
import micro.gymapp.model.Training;

public class TrainingMapper {
    public static TrainingDTO toDTO(Training training, boolean isTrainee) {
        String personName = isTrainee ? training.getTrainer().getFullName() : training.getTrainee().getFullName();

        return new TrainingDTO(
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingType().getTrainingTypeName().name(),
                training.getDuration(),
                personName
        );
    }
}
