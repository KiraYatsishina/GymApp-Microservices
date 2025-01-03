package micro.gymapp.service;

import micro.gymapp.dto.TrainingTypeDTO;
import micro.gymapp.mapper.TrainingTypeMapper;
import micro.gymapp.model.TrainingType;
import micro.gymapp.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;
    public List<TrainingTypeDTO> getAllTrainingTypes() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        return trainingTypes.stream().map(TrainingTypeMapper::toDTO).collect(Collectors.toList());
    }
}
