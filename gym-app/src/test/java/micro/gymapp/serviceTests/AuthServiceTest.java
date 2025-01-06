package micro.gymapp.serviceTests;

import micro.gymapp.Util.JwtCore;
import micro.gymapp.dto.UserDTO;
import micro.gymapp.model.User;
import micro.gymapp.repository.UserRepository;
import micro.gymapp.service.AuthService;
import micro.gymapp.service.LoginAttemptService;
import micro.gymapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtCore jwtCore;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAuthToken_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        when(loginAttemptService.isBlocked("testUser")).thenReturn(false);
        when(userService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtCore.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(new User()));

        ResponseEntity<String> response = authService.createAuthToken(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());
        verify(loginAttemptService).loginSucceeded("testUser");
    }

    @Test
    void testCreateAuthToken_BlockedUser() {
        when(loginAttemptService.isBlocked("blockedUser")).thenReturn(true);

        ResponseEntity<String> response = authService.createAuthToken(new UserDTO("blockedUser", "password"));

        assertEquals(HttpStatus.LOCKED, response.getStatusCode());
        assertEquals("User account is locked due to too many failed login attempts. Try again later.", response.getBody());
    }

    @Test
    void testCreateAuthToken_InvalidCredentials() {
        when(loginAttemptService.isBlocked("testUser")).thenReturn(false);
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseEntity<String> response = authService.createAuthToken(new UserDTO("testUser", "wrongPassword"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Invalid username or password.", response.getBody());
        verify(loginAttemptService).loginFailed("testUser");
    }
}
