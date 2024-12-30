package micro.apigateway.controller;

import micro.apigateway.Util.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gateway")
public class LogoutController {

    @Autowired
    private LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<String> addTokenToBlacklist(@RequestHeader("Authorization") String token) {
        if (token == null && token.isEmpty() || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        String jwt = token.substring(7);
        logoutService.addInvalidToken(jwt);
        return ResponseEntity.ok("Logged out successfully");
    }
}
