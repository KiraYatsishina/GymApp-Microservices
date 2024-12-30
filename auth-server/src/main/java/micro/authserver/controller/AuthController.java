package micro.authserver.controller;

import lombok.RequiredArgsConstructor;
import micro.authserver.client.GymAppClient;
import micro.authserver.dto.UserDTO;
import micro.authserver.dto.SignupTrainee;
import micro.authserver.dto.SignupTrainer;
import micro.authserver.entity.Role;
import micro.authserver.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GymAppClient gymAppClient;

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody UserDTO authRequest) {
        ResponseEntity<?> response = ResponseEntity.status(HttpStatus.OK).body(authService.createAuthToken(authRequest));

        return response;
    }

    @PostMapping("/signup-trainee")
    public ResponseEntity<UserDTO> signupTrainee(@RequestBody SignupTrainee signupTrainee) {
        Optional<UserDTO> userDTO = authService.signUpUser(signupTrainee.getFirstName(), signupTrainee.getLastName(), Role.ROLE_TRAINEE);
        signupTrainee.setUsername(userDTO.get().getUsername());

        return new ResponseEntity<>(userDTO.get(), HttpStatus.OK);
    }

    @PostMapping("/signup-trainer")
    public ResponseEntity<UserDTO> signupTrainer(@RequestBody SignupTrainer signupTrainer) {
        Optional<UserDTO> userDTO = authService.signUpUser(signupTrainer.getFirstName(), signupTrainer.getLastName(), Role.ROLE_TRAINER);
        signupTrainer.setUsername(userDTO.get().getUsername());

        return new ResponseEntity<>(userDTO.get(), HttpStatus.OK);
    }
}
