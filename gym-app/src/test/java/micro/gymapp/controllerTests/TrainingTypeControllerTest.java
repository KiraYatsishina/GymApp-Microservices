package micro.gymapp.controllerTests;

import micro.gymapp.controller.TrainingTypeController;
import micro.gymapp.dto.TrainingTypeDTO;
import micro.gymapp.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTrainingTypes_Success() {
        TrainingTypeDTO trainingType1 = new TrainingTypeDTO(1l,"FITNESS");
        TrainingTypeDTO trainingType2 = new TrainingTypeDTO(2l, "ZUMBA");
        List<TrainingTypeDTO> mockTrainingTypes = Arrays.asList(trainingType1, trainingType2);

        when(trainingTypeService.getAllTrainingTypes()).thenReturn(mockTrainingTypes);

        ResponseEntity<List<TrainingTypeDTO>> response = trainingTypeController.getTrainingTypes();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockTrainingTypes.size(), response.getBody().size());
        assertEquals(mockTrainingTypes, response.getBody());
    }
}
