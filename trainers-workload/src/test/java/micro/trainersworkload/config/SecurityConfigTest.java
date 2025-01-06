package micro.trainersworkload.config;

import micro.trainersworkload.TrainersWorkloadApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TrainersWorkloadApplication.class)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "TRAINEE")
    void accessTrainerEndpoint_withTraineeRole_isForbidden() throws Exception {
        mockMvc.perform(get("/trainer/myProfile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessAnyEndpoint_withoutAuth_isAllowed() throws Exception {
        mockMvc.perform(get("/anyOtherEndpoint"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "TRAINEE")
    void accessTrainerUpdateWorkload_withoutProperRole_isAllowed() throws Exception {
        mockMvc.perform(get("/trainer/update-workload"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser
    void accessSecuredEndpoint_withoutProperRole_isForbidden() throws Exception {
        mockMvc.perform(get("/trainer/secureEndpoint"))
                .andExpect(status().isForbidden());
    }
}