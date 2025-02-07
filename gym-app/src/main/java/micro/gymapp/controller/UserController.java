package micro.gymapp.controller;

import micro.gymapp.dto.ChangeLoginRequest;
import micro.gymapp.model.User;
import micro.gymapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Controller")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @PatchMapping({"/trainee/changeStatus", "/trainer/changeStatus"})
    @Operation(summary = "Change user status", description = "Changes the status of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status changed successfully.", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content)
    })
    public ResponseEntity<?> changeStatus(@RequestParam String username,
                                          @RequestParam boolean status){
        String transactionId = UUID.randomUUID().toString();
        logger.info("Transaction ID: {}, Request to change status of user: {}, New status: {}", transactionId, username, status);

        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            logger.warn("Transaction ID: {}, User {} not found.", transactionId, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        boolean isChanged = userService.changeStatusByUsername(username, status);

        if (isChanged) {
            logger.info("Transaction ID: {}, Status of user {} changed to: {}", transactionId, username, status);
            return ResponseEntity.ok("User status changed successfully.");
        } else {
            logger.warn("Transaction ID: {}, Failed to change status for user {}.", transactionId, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @PatchMapping({"/trainee/changePassword", "/trainer/changePassword"})
    @Operation(summary = "Change user password", description = "Allows authenticated users to change their password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully.", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data or old password is incorrect.", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content)
    })
    public ResponseEntity<?> changePassword(@RequestBody ChangeLoginRequest changeLoginRequest) {
        String transactionId = UUID.randomUUID().toString();
        String username = changeLoginRequest.getUsername();
        logger.info("Transaction ID: {}, Request to change password for user: {}", transactionId, username);

        if (changeLoginRequest.getOldPassword() == null || changeLoginRequest.getNewPassword() == null ||
                changeLoginRequest.getOldPassword().isEmpty() || changeLoginRequest.getNewPassword().isEmpty()) {
            logger.warn("Transaction ID: {}, Invalid request data for user: {}", transactionId, username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data.");
        }

        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            logger.warn("Transaction ID: {}, User {} not found.", transactionId, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        boolean isChanged = userService.changePassword(username, changeLoginRequest.getOldPassword(), changeLoginRequest.getNewPassword());

        if (isChanged) {
            logger.info("Transaction ID: {}, Password for user {} changed successfully.", transactionId, username);
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            logger.warn("Transaction ID: {}, Failed to change password for user {}, old password incorrect.", transactionId, username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect.");
        }
    }
}
