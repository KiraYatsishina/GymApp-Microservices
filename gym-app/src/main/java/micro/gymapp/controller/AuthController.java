package micro.gymapp.controller;

import lombok.RequiredArgsConstructor;

import micro.gymapp.dto.UserDTO;
import micro.gymapp.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody UserDTO authRequest) {
        ResponseEntity<String> response = authService.createAuthToken(authRequest);

        return response;
    }
}
