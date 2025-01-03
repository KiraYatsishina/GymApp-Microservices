package micro.gymapp.mapper;


import micro.gymapp.dto.Trainee.ShortTraineeDTO;
import micro.gymapp.dto.Trainee.TraineeDTO;
import micro.gymapp.model.Trainee;

import java.util.stream.Collectors;

public class TraineeMapper {
    public static TraineeDTO toDTO(Trainee trainee, boolean hasTrainerList) {
        return new TraineeDTO(
                trainee.getUsername(),
                trainee.getFirstName(),
                trainee.getLastName(),
                trainee.isActive(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                hasTrainerList ? trainee.getTrainers().stream()
                        .map(TrainerMapper::toShortDTO)
                        .collect(Collectors.toList()) : null
        );
    }

    public static ShortTraineeDTO toShortDTO(Trainee trainee) {
        return new ShortTraineeDTO(
                trainee.getUsername(),
                trainee.getFirstName(),
                trainee.getLastName()
        );
    }
}