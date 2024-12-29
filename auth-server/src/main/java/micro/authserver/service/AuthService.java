package micro.authserver.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import micro.authserver.dto.UserDTO;
import micro.authserver.entity.Role;
import micro.authserver.entity.Token;
import micro.authserver.entity.User;
import micro.authserver.repository.TokenRepository;
import micro.authserver.repository.UserRepository;
import micro.authserver.util.JwtCore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginAttemptService loginAttemptService;
    private final TokenRepository tokenRepository;
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
        revokeAllByUser(user);
        saveToken(user.get(), jwt);

        return ResponseEntity.ok(jwt);
    }

    public void revokeAllByUser(Optional<User> user) {
        List<Token> validTokenByUser = tokenRepository.findAllAccessTokensByUser(user.get().getUserId());
        if(!validTokenByUser.isEmpty()){
            validTokenByUser.forEach(token -> token.setLoggedOut(true));
        }
        tokenRepository.saveAll(validTokenByUser);
    }

    private void saveToken(User user, String jwt) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    public Optional<UserDTO> signUpUser(String firstname, String lastname, Role role) {
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

        User savedUser = userRepository.save(user);

        UserDTO userDTO = UserDTO.builder()
                .username(savedUser.getUsername())
                .password(generatedPassword)
                .build();

        return Optional.of(userDTO);
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

    public boolean validateToken(String jwt) {
        String[] parts = jwt.split(" ");
        jwt = parts[1];
       //jwtTokenUtils.validateToken(jwt);

        Optional<Token> tokenEntity = tokenRepository.findByToken(jwt);
        if(!tokenEntity.isPresent()) return false;

        Token token = tokenEntity.get();
        if (token.isLoggedOut()) return false;

        String username = jwtTokenUtils.getUsername(jwt);
        if (loginAttemptService.isBlocked(username)) return false;

        if (jwtTokenUtils.isExpired(jwt)) return false;

        return true;
    }
}
