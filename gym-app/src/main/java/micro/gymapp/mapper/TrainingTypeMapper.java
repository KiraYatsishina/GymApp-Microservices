package micro.gymapp.mapper;

import micro.gymapp.dto.TrainingTypeDTO;
import micro.gymapp.model.TrainingType;

public class TrainingTypeMapper {
    public static TrainingTypeDTO toDTO(TrainingType trainingType) {
        return new TrainingTypeDTO(
                trainingType.getId(),
                trainingType.getTrainingTypeName().name()
        );
    }
}
