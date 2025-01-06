package micro.gymapp.service;

import lombok.RequiredArgsConstructor;

import micro.gymapp.Util.JwtCore;
import micro.gymapp.dto.UserDTO;
import micro.gymapp.model.User;
import micro.gymapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginAttemptService loginAttemptService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtCore jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<String> createAuthToken(UserDTO userCredentials) {
        String username = userCredentials.getUsername();
        if(loginAttemptService.isBlocked(username)){
            return ResponseEntity.status(HttpStatus.LOCKED).
                    body("User account is locked due to too many failed login attempts. Try again later.");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, userCredentials.getPassword()));
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid username or password.");
        }
        loginAttemptService.loginSucceeded(username);
        UserDetails userDetails = userService.loadUserByUsername(username);
        String jwt = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(jwt);
    }
}
