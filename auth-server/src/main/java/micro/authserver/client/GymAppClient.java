package micro.authserver.client;

import micro.authserver.dto.SignupTrainee;
import micro.authserver.dto.SignupTrainer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gymapp")
public interface GymAppClient {

    @RequestMapping("/signup/trainee")
    ResponseEntity<?> signupTrainee(SignupTrainee signupTrainee);

    @RequestMapping("/signup/trainer")
    ResponseEntity<?> signupTrainer(SignupTrainer signupTrainer);
}
