package micro.gymapp.controllerTests;

import micro.gymapp.controller.UserController;
import micro.gymapp.dto.ChangeLoginRequest;
import micro.gymapp.model.User;
import micro.gymapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setPassword("oldPassword");
        mockUser.setActive(true);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testChangeStatus_Success() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(userService.changeStatusByUsername("testUser", true)).thenReturn(true);

        MvcResult result = mockMvc.perform(patch("/trainee/changeStatus?status=true")
                        .principal(() -> "testUser"))
                .andExpect(status().isOk())
                .andExpect(content().string("User status changed successfully."))
                .andReturn();

        verify(userService, times(1)).findByUsername("testUser");
        verify(userService, times(1)).changeStatusByUsername("testUser", true);
    }

    @Test
    void testChangeStatus_UserNotFound() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.empty());

        mockMvc.perform(patch("/trainee/changeStatus?status=true")
                        .principal(() -> "testUser"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."));

        verify(userService, times(1)).findByUsername("testUser");
        verify(userService, never()).changeStatusByUsername(anyString(), anyBoolean());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest("oldPassword", "newPassword");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(userService.changePassword("testUser", "oldPassword", "newPassword")).thenReturn(true);

        mockMvc.perform(patch("/trainee/changePassword")
                        .principal(() -> "testUser")
                        .contentType("application/json")
                        .content("{\"oldPassword\":\"oldPassword\",\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully."));

        verify(userService, times(1)).findByUsername("testUser");
        verify(userService, times(1)).changePassword("testUser", "oldPassword", "newPassword");
    }

    @Test
    void testChangePassword_UserNotFound() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest("oldPassword", "newPassword");

        when(userService.findByUsername("testUser")).thenReturn(Optional.empty());

        mockMvc.perform(patch("/trainee/changePassword")
                        .principal(() -> "testUser")
                        .contentType("application/json")
                        .content("{\"oldPassword\":\"oldPassword\",\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."));

        verify(userService, times(1)).findByUsername("testUser");
        verify(userService, never()).changePassword(anyString(), anyString(), anyString());
    }

    @Test
    void testChangePassword_InvalidRequestData() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest("", "");

        mockMvc.perform(patch("/trainee/changePassword")
                        .principal(() -> "testUser")
                        .contentType("application/json")
                        .content("{\"oldPassword\":\"\",\"newPassword\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request data."));

        verify(userService, never()).findByUsername("testUser");
        verify(userService, never()).changePassword(anyString(), anyString(), anyString());
    }

    @Test
    void testChangePassword_OldPasswordIncorrect() throws Exception {
        ChangeLoginRequest request = new ChangeLoginRequest("incorrectOldPassword", "newPassword");

        when(userService.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(userService.changePassword("testUser", "incorrectOldPassword", "newPassword")).thenReturn(false);

        mockMvc.perform(patch("/trainee/changePassword")
                        .principal(() -> "testUser")
                        .contentType("application/json")
                        .content("{\"oldPassword\":\"incorrectOldPassword\",\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Old password is incorrect."));

        verify(userService, times(1)).findByUsername("testUser");
        verify(userService, times(1)).changePassword("testUser", "incorrectOldPassword", "newPassword");
    }
}
