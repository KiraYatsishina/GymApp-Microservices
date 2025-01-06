package micro.gymapp.configTest;

import micro.gymapp.GymApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GymApplication.class)
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
    @WithMockUser(roles = "USER")
    void accessAnySecuredEndpoint_withoutProperRole_isUnauthorized() throws Exception {
        mockMvc.perform(get("/trainee/myProfile"))
                .andExpect(status().isForbidden());
    }
}
