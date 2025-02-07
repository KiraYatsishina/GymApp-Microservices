package micro.gymapp.unit.controllerTests;

import micro.gymapp.controller.AuthController;
import micro.gymapp.dto.UserDTO;
import micro.gymapp.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    private final AuthService authService = Mockito.mock(AuthService.class);
    private final AuthController authController = new AuthController(authService);

    @Test
    void getToken_validUserDTO_shouldReturnOkStatus() {
        UserDTO userDTO = new UserDTO("testUser", "password123");
        String mockToken = "mockAuthToken";
        when(authService.createAuthToken(userDTO)).thenReturn(ResponseEntity.ok(mockToken));

        ResponseEntity<String> response = authController.getToken(userDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockToken, response.getBody());
    }

    @Test
    void getToken_invalidUserDTO_shouldReturnErrorStatus() {
        UserDTO userDTO = new UserDTO("testUser", "wrongPassword");
        when(authService.createAuthToken(userDTO)).thenReturn(ResponseEntity.status(404).body("Invalid username or password."));

        ResponseEntity<String> response = authController.getToken(userDTO);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Invalid username or password.", response.getBody());
    }

    @Test
    void getToken_lockedUser_shouldReturnLockedStatus() {
        UserDTO userDTO = new UserDTO("lockedUser", "password123");
        when(authService.createAuthToken(userDTO)).thenReturn(ResponseEntity.status(423).body("User account is locked due to too many failed login attempts. Try again later."));

        ResponseEntity<String> response = authController.getToken(userDTO);

        assertEquals(423, response.getStatusCodeValue());
        assertEquals("User account is locked due to too many failed login attempts. Try again later.", response.getBody());
    }
}