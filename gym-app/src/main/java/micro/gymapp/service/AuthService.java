package micro.gymapp.service;

import lombok.RequiredArgsConstructor;

import micro.gymapp.Util.JwtCore;
import micro.gymapp.dto.UserDTO;
import micro.gymapp.model.Role;
import micro.gymapp.model.User;
import micro.gymapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginAttemptService loginAttemptService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtCore jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

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

        Optional<User> user = userRepository.findByUsername(username);


        return ResponseEntity.ok(jwt);
    }



    public User signUpUser(String firstname, String lastname, Role role) {
        long count = userRepository.countByFirstNameAndLastName(firstname, lastname);
        String username = generateUniqueUsername(firstname, lastname, count);
        String generatedPassword = generatePassword();
        String encodedPassword = passwordEncoder.encode(generatedPassword);

        User user = new User();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setActive(true);

        return userRepository.save(user);
    }

    private String generateUniqueUsername(String firstName, String lastName, long count) {
        return firstName + "." + lastName + (count > 0 ? count : "");
    }

    private String generatePassword() {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder passwordBuilder = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            passwordBuilder.append(characters.charAt(index));
        }
        return passwordBuilder.toString();
    }
}
