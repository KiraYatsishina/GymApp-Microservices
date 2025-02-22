package micro.gymapp.service;

import micro.gymapp.dto.Trainee.SignupTrainee;
import micro.gymapp.dto.Trainee.TraineeDTO;
import micro.gymapp.dto.Trainee.UpdateTraineeDTO;
import micro.gymapp.dto.Trainer.ShortTrainerDTO;
import micro.gymapp.dto.UserDTO;
import micro.gymapp.mapper.TraineeMapper;
import micro.gymapp.mapper.TrainerMapper;
import micro.gymapp.model.Role;
import micro.gymapp.model.Trainee;
import micro.gymapp.model.Trainer;
import micro.gymapp.repository.TraineeRepository;
import lombok.RequiredArgsConstructor;
import micro.gymapp.repository.TrainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserService userService;

    public Optional<TraineeDTO> findByUsername(String username) {
        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        return trainee.map(t -> TraineeMapper.toDTO(t, true));
    }

    public Trainee mapToEntity(SignupTrainee traineeDTO) {
        if (traineeDTO == null) {
            logger.warn("SignupTrainee DTO is null");
            return null;
        }
        Trainee trainee = new Trainee();
        trainee.setFirstName(traineeDTO.getFirstName());
        trainee.setLastName(traineeDTO.getLastName());
        trainee.setDateOfBirth(traineeDTO.getDateOfBirth());
        trainee.setAddress(traineeDTO.getAddress());
        return trainee;
    }

    @Transactional
    public Optional<Trainee> updateTraineeProfile(UpdateTraineeDTO updateTraineeDTO) {
        String username = updateTraineeDTO.getUsername();
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        if (traineeOptional.isPresent()) {
            Trainee trainee = traineeOptional.get();

            trainee.setFirstName(updateTraineeDTO.getFirstName());
            trainee.setLastName(updateTraineeDTO.getLastName());
            trainee.setDateOfBirth(updateTraineeDTO.getDateOfBirth());
            trainee.setAddress(updateTraineeDTO.getAddress());
            trainee.setActive(updateTraineeDTO.isActive());
            return Optional.of(traineeRepository.save(trainee));
        }
        return Optional.empty();
    }

    @Transactional
    public List<ShortTrainerDTO> getNotAssignedTrainersList(String username){
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        List<Trainer> notAssignedTrainers = traineeRepository.findNotAssignedTrainers(traineeOptional.get().getUserId());

        return notAssignedTrainers.stream()
                .map(TrainerMapper::toShortDTO).collect(Collectors.toList());
    }

    @Transactional
    public List<ShortTrainerDTO> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Trainer> trainers = trainerRepository.findByUsernameIn(trainerUsernames);

        if (trainers.size() != trainerUsernames.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        trainee.setTrainers(trainers);
        traineeRepository.save(trainee);

        return trainers.stream()
            .map(TrainerMapper::toShortDTO).collect(Collectors.toList());
    }

    public long countActiveTrainees() {
        return traineeRepository.countByIsActive(true);
    }

    public Optional<UserDTO> signUpTrainee(Trainee trainee) {
        return userService.signUpUser(trainee, Role.ROLE_TRAINEE, traineeRepository::save);
    }
}
